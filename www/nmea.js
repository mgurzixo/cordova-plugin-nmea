var exec = require('cordova/exec');

exports.watch = function (success, error) {
    exec(success, error, 'Nmea', 'watch', []);
};

exports.clearWatch = function (success, error) {
    exec(success, error, 'Nmea', 'clearWatch', []);
};
