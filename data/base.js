// Function that is called when a link to a specific paper is clicked
function showPaper(paperID) {
    // Expand the abstract if it is currently hidden, otherwise leave it expanded
    var bibitem = $(paperID);
    var abs = bibitem.children('.abstract-container');

    if (abs.length && abs.is(":hidden")) {
        toggleAbstract(abs);

        // Update the button text to reflect that the abstract expanded
        var button = bibitem.children('button.abstract-toggle');

        if (button.length && button.text() == "Abstract") {
            toggleText(button, "Abstract", "Hide");
        }
    }
}

// Toggles abstract. Input: .abstract-container
function toggleAbstract(abs) {
    abs.slideToggle(500);

    // Render the math in here, if necessary
    if (abs.hasClass("tex2jax_ignore")) {
        abs.removeClass("tex2jax_ignore");
        MathJax.Hub.Queue(["Typeset", MathJax.Hub, abs.get(0)]);
    }
}

// Toggles button text
function toggleText(button, text1, text2) {
    // Store the original width, so it doesn't change
    var width = button.width();

    // Swap the labels
    if (button.text() == text1) {
        button.text(text2);
    } else {
        button.text(text1);
    }

    // Reset the width to the original
    button.width(width);
}

// Functions that run the first time the document loads
$(document).ready(function() {
    // Abstracts start hidden for JS-enabled users
    $('.abstract-container').hide();

    // Detection for links to specific articles
    // Run once, for page reloads
    var hash = window.location.hash;
    if (hash) {
        showPaper(hash);
    }

    // Register an event listener, to run every time the hash changes
    $(window).on('hashchange', function() {
        showPaper(document.location.hash);
    });

    // Add click-behaviour to toggle elements
    $('.abstract-toggle').click(function () {
        toggleAbstract($(this).parent().children('.abstract-container'));
    });
    $('button.abstract-toggle').click(function () {
        toggleText($(this), "Abstract", "Hide");
    });

    $('.bibtex-toggle').click(function () {
        $(this).parent().children('.bibtex-container').slideToggle(500);
    });
    $('button.bibtex-toggle').click(function () {
        toggleText($(this), "BibTeX", "Hide");
    });
});
