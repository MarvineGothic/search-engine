$(document).ready(function () {
    var baseUrl = "http://localhost:8080";
    var availableTags = getCookie('_searches');

    /**
     * This function splits a string by whitespaces
     * @param val
     * @returns {*|Array}
     */
    function split(val) {
        return val.split(/\s+/);
    }

    /**
     * This function splits a string by whitespaces
     * and returns last string
     * @param term
     * @returns {*}
     */
    function extractLast(term) {
        return split(term).pop();
    }

    /**
     * This function replaces all characters except letters, digits and whitespaces
     * by a single whitespace
     * @param term
     * @returns {*|Array}
     */
    function replace(term) {
        var str = term.replace(/[^\w\s]|_/g, " ")
            .replace(/\s+/g, " ");
        return split(str);
    }

    /**
     * Returns boolean value if array contains a specific word
     * @param array
     * @param word
     * @returns {boolean}
     */
    function contains(array, word) {
        return (array.indexOf(word.toLowerCase()) > -1);
    }

    /**
     * Returns a string of words from a cookie file
     * @param cName
     * @returns {*|String}
     */
    function getCookie(cName) {
        var name = cName + "=";
        var ca = document.cookie.split(';');
        for (var i = 0; i < ca.length; i++) {
            var c = ca[i];
            while (c.charAt(0) === ' ') c = c.substring(1);
            if (c.indexOf(name) === 0) return c.substring(name.length, c.length);
        }
        return "";
    }

    /**
     * This function updates a cookie file, based on a query search.
     * It splits a multi word query in single words, sets them to lower case and
     * adds only unique words in the cookie file.
     * @param event
     */
    function changeCookie(event) {
        var s = getCookie('_searches');
        var array = replace(event.target.value);
        var result = "";
        for (var i = 0; i < array.length; i++) {
            var word = array[i];
            if (!contains(availableTags, word) && !contains(result.split(' '), word) && word !== "OR") {
                result = result.concat(' ' + array[i]);
            }
        }
        document.cookie = "_searches=" + s + result.toLowerCase();
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
            if (data.length === 0)
                $("#responsesize").html("<p>No website contains the query word.</p>");
            else
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
    // it clicks a button by pressing "Enter" key
    // and adds a query words to cookie file
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
            minLength: 1,
            // update `cookie` with `value` of `#searchbox`, on `change` event
            change: function (event) {
                var s = getCookie('_searches');
                var array = replace(event.target.value);
                var result = "";
                for (var i = 0; i < array.length; i++) {
                    var word = array[i];
                    if (!contains(availableTags, word) && !contains(result.split(' '), word) && word !== "OR") {
                        result = result.concat(' ' + array[i]);
                    }
                }
                document.cookie = "_searches=" + s + result.toLowerCase();
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
                // add placeholder
                terms.push("");
                this.value = terms.join(" ");
                return false;
            }
        });
});
