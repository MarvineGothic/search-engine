$(document).ready(function () {
    var baseUrl = "http://localhost:8080";
    var availableTags = getCookie('_searches')/*.split('\\s+')*/;

    /**
     * functions
     * @param val
     * @returns {*|Array}
     */
    function split(val) {
        return val.split(/\s+/);
    }

    function extractLast(term) {
        return split(term).pop();
    }

    function replace(term) {
        var str = term.replace(/[^\w\s]|_/g, " ")
            .replace(/\s+/g, " ");
        return split(str);
    }

    function contains(array, word) {
        return (array.indexOf(word.toLowerCase()) > -1);
    }

    function getCookie(cname) {
        var name = cname + "=";
        var ca = document.cookie.split(';');
        for (var i = 0; i < ca.length; i++) {
            var c = ca[i];
            while (c.charAt(0) === ' ') c = c.substring(1);
            if (c.indexOf(name) === 0) return c.substring(name.length, c.length);
        }
        return "";
    }

    function changeCookie(event) {
        var s = getCookie('_searches');
        var array = replace(event.target.value);
        var result = "";
        for (var i = 0; i < array.length; i++) {
            var word = array[i].toLowerCase();
            if (!contains(availableTags, word) && !contains(result.split(' '), word)) {
                result = result.concat(' ' + word);
            }
        }
        document.cookie = "_searches=" + s + result;
    }

    /**
     * running scripts "#searchbutton" and "#searchbox"
     */
    $("#searchbutton").click(function () {
        console.log("Sending request to server.");
        $.ajax({
            method: "GET",
            url: baseUrl + "/search",
            data: {query: $('#searchbox').val()}
        }).success(function (data) {
            console.log("Received response " + data);
            $("#responsesize").html("<p>" + data.length + " websites retrieved</p>");
            var buffer = "<ul>\n";
            $.each(data, function (index, value) {
                buffer += "<li><a href=\"" + value.url + "\">" + value.title + "</a></li>\n";
            });
            buffer += "</ul>";
            $("#urllist").html(buffer);
        });
    });

    $("#searchbox")
        .keypress(function (event) {
            if (event.keyCode === 13) {
                $('#searchbutton').click();
                changeCookie(event);
            }

        })
        // don't navigate away from the field on tab when selecting an item
        .on("keydown", function (event) {
            if (event.keyCode === $.ui.keyCode.TAB && $(this).autocomplete("instance").menu.active) {
                event.preventDefault();
            }
        })
        .autocomplete({
            minLength: 2,
            // update `cookie` with `value` of `#searchbox`, on `change` event
            change: function (event) {
                var s = getCookie('_searches');
                var array = replace(event.target.value);
                var result = "";
                for (var i = 0; i < array.length; i++) {
                    var word = array[i].toLowerCase();
                    if (!contains(availableTags, word) && !contains(result.split(' '), word)) {
                        result = result.concat(' ' + word);
                    }
                }
                document.cookie = "_searches=" + s + result;
            },
            source: function (request, response) {
                availableTags = getCookie('_searches').split(' ');
                // delegate back to autocomplete, but extract the last term
                response($.ui.autocomplete.filter(availableTags, extractLast(request.term)));
            },
            focus: function () {
                // prevent value inserted on focus
                return false;
            },
            select: function (event, ui) {
                var terms = split(this.value);
                // remove the current input
                terms.pop();
                // add the selected item
                terms.push(ui.item.value);
                // add placeholder to get the comma-and-space at the end
                terms.push("");
                this.value = terms.join(" ");
                return false;
            }
        });
});
