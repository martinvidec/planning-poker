window.pp = {

    countdown: null,
    targetDate: null,
    element: null,
    text: null,

    cancelCountdown: function() {
      if (window.pp.countdown == null) {
          return;
      }
        window.pp.reset();
    },

    startCountdown: function (targetDateAsString, elementId, timeoutText) {
        if(window.pp.countdown != null) {
            return;
        }
        // Get today's date and time
        // Find the distance between now and the count down date
        window.pp.targetDate = new Date(targetDateAsString).getTime();
        // display element
        window.pp.element = document.getElementById(elementId);
        // text to display when finished
        window.pp.text = timeoutText;
        // call function the first time directly
        window.pp.updateCountdown();
        // if for some reasons distance was already reached don't call periodic callback
        if(window.pp.targetDate == null) {
            return;
        }
        // reference to periodic callback
        window.pp.countdown = setInterval(window.pp.updateCountdown, 1000);
    },

    updateCountdown: function () {
        let distance = window.pp.targetDate - (new Date().getTime());

        // If the count down is over, write some text
        if (distance < 0) {
            let text = window.pp.text;
            window.pp.reset();
            window.pp.element.innerHTML = text;
            return;
        }

        // Time calculations for days, hours, minutes and seconds
        // let days = Math.floor(distance / (1000 * 60 * 60 * 24));
        // let hours = Math.floor((distance % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
        let minutes = Math.floor((distance % (1000 * 60 * 60)) / (1000 * 60));
        let seconds = Math.floor((distance % (1000 * 60)) / 1000);

        // Output the result in an element
        window.pp.element.innerHTML = (minutes < 10 ? "0" : "") + minutes + ":" + (seconds < 10 ? "0" : "") + seconds;
    },

    reset: function() {
        // stop countdown
        clearInterval(window.pp.countdown);
        // reset values
        window.pp.countdown = null;
        window.pp.targetDate = null;
        window.pp.text = null;
        // clear display but retain display-element
        window.pp.element.innerHTML = "";
    }
};
