Graphs = {

    libsGraph: function (data) {
        var libClassData = [
            {
                value: data.compatible,
                color: "green",
                highlight: "#00b300",
                label: "Compatible",
                labelColor: 'red',
                labelFontSize: '16'
            },
            {
                value: data.redundant,
                color: "grey",
                highlight: "#a6a6a6",
                label: "Redundant",
                labelColor: 'red',
                labelFontSize: '16'
            },
            {
                value: data.incompatible,
                color: "red",
                highlight: "#ff4d4d",
                label: "Incompatible",
                labelColor: 'red',
                labelFontSize: '16'
            },
            {
                value: data.mustRemove,
                color: "darkred",
                highlight: "#b30000",
                label: "Must Remove",
                labelColor: 'red',
                labelFontSize: '16'
            }
        ];

        var librariesChartCtx = $("#librariesChart").get(0).getContext("2d");
        var librariesChartOptions = {};

        var librariesPieChart = new Chart(librariesChartCtx).Doughnut(libClassData, librariesChartOptions);
        var legend = librariesPieChart.generateLegend();
        $("#librariesChartLegend").append(legend);

    },

    problemsGraph: function (inputData) {
        var problemsChartData = {
            labels: [
                "C1",
                "C2",
                "C3",
                "M1",
                "M2",
                "F1",
                "F2",
                "MOD",
                "M.M1",
                "F7"],

            datasets: [
                {
                    label: "Classification by Problems",
                    fillColor: "#FACFCA",
                    strokeColor: "#F22929",
                    highlightFill: "#FFEAE8",
                    highlightStroke: "#F22929",
                    data: [
                        inputData.c1,
                        inputData.c2,
                        inputData.c3,
                        inputData.m1,
                        inputData.m2,
                        inputData.f1,
                        inputData.f2,
                        inputData.mod,
                        inputData.mm1,
                        inputData.f7
                    ]
                }
            ]
        };

        var problemsChartCtx = $("#problemsChart").get(0).getContext("2d");
        var problemsChartOptions = [];
        var problemsChartChart = new Chart(problemsChartCtx).Bar(problemsChartData, problemsChartOptions);
    }

};

Reachable = {

    // Set CSS class reachable for parents of "importingClass"
    // It means for problem holders and exporters
    calculateCssReachableParents: function () {

        var problemHolders = $(".problemHolder");
        var arrayLength = problemHolders.length;
        for (var i = 0; i < arrayLength; i++) {
            Reachable.calculateCssReachableProblemHolder(problemHolders[i]);
            Reachable.calculateCssReachableExporters(problemHolders[i]);
        }
    },

    calculateCssReachableProblemHolder: function (problemHolder) {
        var reachableClasses = $(problemHolder).find(".reachable");
        var nonEmptyCount = reachableClasses.length;

        // we have no reachable class
        if (nonEmptyCount == 0) {
            $(problemHolder).addClass("nonReachable");
        } else {
            $(problemHolder).addClass("reachable");
        }
    },

    calculateCssReachableExporters: function (problemHolder) {
        var importers = $(problemHolder).find(".importers");

        var arrayLength = importers.length;
        for (var i = 0; i < arrayLength; i++) {
            var importer = importers[i];
            var nonEmptyCount = $(importer).find(".reachable").length;
            var exporter = $(importer).next(".exporters");

            // non-hidden panel with only empty elements will be hidden, otherwise shown
            if (nonEmptyCount == 0) {
                $(exporter).addClass("nonReachable");
                $(importer).addClass("nonReachable");
            } else {
                $(exporter).addClass("reachable");
                $(importer).addClass("reachable");
            }
        }
    }


};

