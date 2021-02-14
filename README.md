[![GitHub version](https://badge.fury.io/gh/kyosho-%2Fcordova-plugin-nmea.svg)](https://badge.fury.io/gh/kyosho-%2Fcordova-plugin-nmea)
[![npm version](https://badge.fury.io/js/cordova-plugin-nmea.svg)](https://badge.fury.io/js/cordova-plugin-nmea)
![GitHub](https://img.shields.io/github/license/kyosho-/cordova-plugin-nmea)
[![GitHub issues](https://img.shields.io/github/issues/kyosho-/cordova-plugin-nmea)](https://github.com/kyosho-/cordova-plugin-nmea/issues)
[![GitHub forks](https://img.shields.io/github/forks/kyosho-/cordova-plugin-nmea)](https://github.com/kyosho-/cordova-plugin-nmea/network)
[![GitHub stars](https://img.shields.io/github/stars/kyosho-/cordova-plugin-nmea)](https://github.com/kyosho-/cordova-plugin-nmea/stargazers)

# cordova-plugin-nmea
cordova plugin for NMEA Listener 

## Install

```
$ cordova plugin add cordova-plugin-nmea
```

## Usage

* watch
* clearWatch

### Watch NMEA from GPS device

```js
cordova.plugins.nmea.watch(
    function(result) {
        subscriber.next(result);
    },
    function(error) {
        subscriber.error(error);
    });
```

```json
{
    "id": "watch"
}
```

```json
{
    "timestamp": 1572665074340,
    "message": "$GLGSV,2,2,07,69,51,327,,83,26,191,,85,18,327,*5B"
}
```

### Clear watching

```js
cordova.plugins.nmea.clearWatch(
    function(result) {
        subscriber.next(result);
    },
    function(error) {
        subscriber.error(error);
    });
```

```json
{
    "id": "clearWatch"
}
```

# License - MIT

```
MIT License

Copyright (c) 2019 Akira Kurosawa

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
