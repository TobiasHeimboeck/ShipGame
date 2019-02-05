const main = new Vue({
    el: "#main",
    data: {
        pageNumber: {},
        gameData: {}
    },
    created() {
        this.loadPage("gp");
        this.startFetchingAsync("/api/game_view/" + this.getParameterByName("gp"));
    },
    methods: {
        startFetchingAsync(url) {
            fetch(url, {
                method: "GET",
            }).then(function (response) {

                return response.json();

            }).then(function (json) {

                data = json;
                this.gameData = data;
                console.log(this.gameData);

            }).catch(function (error) {
                console.log(error);
            })
        },
        getParameterByName(name, url) {
            if (!url) url = window.location.href;
            name = name.replace(/[\[\]]/g, '\\$&');
            var regex = new RegExp('[?&]' + name + '(=([^&#]*)|&|#|$)'),
                results = regex.exec(url);
            if (!results) return null;
            if (!results[2]) return '';
            return decodeURIComponent(results[2].replace(/\+/g, ' '));
        },
        loadPage(search) {
            var reg = /(?:[?&]([^?&#=]+)(?:=([^&#]*))?)(?:#.*)?/g;

            search.replace(reg, function (match, param, val) {
                this.pageNumber[decodeURIComponent(param)] = val === undefined ? "" : decodeURIComponent(val);
            });

            return this.pageNumber;
        },
    }
});
