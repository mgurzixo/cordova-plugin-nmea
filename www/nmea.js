var exec = require("cordova/exec");

const SPEED_THRESHOLD_MPS = 0.5;
const POSITION_ALPHA = 0.15;

class NmeaSmoother {
    constructor() {
        this.filtered = null;
    }

    reset() {
        this.filtered = null;
    }

    update({ latitude, longitude, altitude, speed }) {
        if (speed >= SPEED_THRESHOLD_MPS || !this.filtered) {
            this.filtered = { latitude, longitude, altitude };
            return { latitude, longitude, altitude };
        }

        this.filtered = {
            latitude: lerp(this.filtered.latitude, latitude, POSITION_ALPHA),
            longitude: lerp(this.filtered.longitude, longitude, POSITION_ALPHA),
            altitude: lerp(this.filtered.altitude, altitude, POSITION_ALPHA),
        };
        return { ...this.filtered };
    }
}

function lerp(a, b, alpha) {
    return a + (b - a) * alpha;
}

var nmea = {
    watch: function (success, error) {
        exec(success, error, "Nmea", "watch", []);
    },

    clearWatch: function (success, error) {
        exec(success, error, "Nmea", "clearWatch", []);
    },

    createSmoother: function () {
        return new NmeaSmoother();
    },

    NmeaSmoother,
};

module.exports = nmea;
