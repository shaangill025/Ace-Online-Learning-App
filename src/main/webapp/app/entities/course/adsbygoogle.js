module.exports = {
    /**
     *  Check if an ad blocker is detected
     *  @return Boolean
     */
    isDetected: function(){
        var detected = false;

        // create the bait
        var bait = document.createElement('div');
        bait.id = 'nWarBteAmkpQ';
        bait.style.display='none';
        window.document.body.appendChild(bait);

        // check if the bait has been affected by an adblocker
        if (document.getElementById('nWarBteAmkpQ')) {
            detected = false;
        } else {
            detected = true;
        }
        // destroy the bait
        window.document.body.removeChild(bait);

        return detected;
    }
};
