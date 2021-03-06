﻿this["Perf"] && void 0 !== this["Perf"].enabled || (function (window) {
    'use strict';

    function a() {
        return function () { }
    }

    function b(e) {
        return function () {
            return e
        }
    }
    var c = {
        DEBUG: {
            name: "DEBUG",
            value: 1
        },
        INTERNAL: {
            name: "INTERNAL",
            value: 2
        },
        PRODUCTION: {
            name: "PRODUCTION",
            value: 3
        },
        DISABLED: {
            name: "DISABLED",
            value: 4
        }
    };
    window.PerfConstants = {
        PAGE_START_MARK: "PageStart",
        PERF_PAYLOAD_PARAM: "bulkPerf",
        MARK_NAME: "mark",
        MEASURE_NAME: "measure",
        MARK_START_TIME: "st",
        MARK_LAST_TIME: "lt",
        PAGE_NAME: "pn",
        ELAPSED_TIME: "et",
        REFERENCE_TIME: "rt",
        Perf_LOAD_DONE: "loadDone",
        STATS: {
            NAME: "stat",
            SERVER_ELAPSED: "internal_serverelapsed",
            DB_TOTAL_TIME: "internal_serverdbtotaltime",
            DB_CALLS: "internal_serverdbcalls",
            DB_FETCHES: "internal_serverdbfetches"
        }
    };
    window.PerfLogLevel = c;
    var d = window.Perf = {
        currentLogLevel: c.DISABLED,
        mark: function () {
            return d
        },
        endMark: function () {
            return d
        },
        updateMarkName: function () {
            return d
        },
        measureToJson: b(""),
        toJson: b(""),
        setTimer: function () {
            return d
        },
        toPostVar: b(""),
        getMeasures: function () {
            return []
        },
        getBeaconData: b(null),
        setBeaconData: a(),
        clearBeaconData: a(),
        removeStats: a(),
        stat: function () {
            return d
        },
        getStat: b(-1),
        onLoad: a(),
        startTransaction: function () {
            return d
        },
        endTransaction: function () {
            return d
        },
        updateTransaction: function () {
            return d
        },
        isOnLoadFired: b(!1),
        util: {
            setCookie: a()
        },
        enabled: !1
    };
})(this);