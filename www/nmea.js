var exec = require("cordova/exec");

var nmea = {
    watch: function (success, error) {
        exec(success, error, "Nmea", "watch", []);
    },

    clearWatch: function (success, error) {
        exec(success, error, "Nmea", "clearWatch", []);
    },
};

module.exports = nmea;
