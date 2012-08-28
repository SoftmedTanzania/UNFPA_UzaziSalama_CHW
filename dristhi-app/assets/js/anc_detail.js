function ANC(ancBridge) {
    return {
        populateInto: function (cssIdentifierOfRootElement) {
            $(cssIdentifierOfRootElement).html(Handlebars.templates.anc_detail(ancBridge.getCurrentANC()));
        },

        bindEveryItemToCommCare: function (cssIdentifierOfRootElement, cssClassOfChildElement) {
            $(cssIdentifierOfRootElement).on("click", cssClassOfChildElement, function (event) {
                ancBridge.delegateToCommCare($(this).data("form"), $(this).data("caseid"));
            });
        },

        bindItemToCommCare: function(cssIdentifierOfElement) {
            $(cssIdentifierOfElement).click(function () {
                ancBridge.delegateToCommCare($(this).data("form"), $(this).data("caseid"));
            })
        }
    };
}

function ANCBridge() {
    var ancContext = window.context;
    if (typeof ancContext === "undefined" && typeof FakeANCContext !== "undefined") {
        ancContext = new FakeANCContext();
    }

    return {
        getCurrentANC: function () {
            return JSON.parse(ancContext.get());
        },

        delegateToCommCare: function (formId, caseId) {
            ancContext.startCommCare(formId, caseId);
        }
    };
}

function FakeANCContext() {
    return {
        startCommCare: function (formId, caseId) {
            alert("Start CommCare with form " + formId + " on case with caseId: " + caseId);
        },
        get: function () {
            return JSON.stringify({
                    womanName: "Wife 1",
                    caseId: "1234",
                    thaayiCardNumber: "TC Number 1",
                    location: {
                        villageName: "Village 1",
                        subcenter: "SubCenter 1"
                    },
                    pregnancyDetails: {
                        isHighRisk: true,
                        riskDetail: "Anaemia (active): 21 months",
                        monthsPregnant: "7",
                        edd: "24/12/12"
                    },
                    facilityDetails: {
                        facility: "Broadway",
                        ashaName: "Shiwani",
                        contact: "----"
                    },
                    alerts: [
                        {
                            message: "Alert 1",
                            formToOpen: "ANC_SERVICES"
                        },
                        {
                            message: "Alert 2",
                            formToOpen: "ANC_SERVICES"
                        }
                    ],
                    todos: [
                        {
                            message: "IFA Tablet follow-up",
                            formToOpen: "ANC_SERVICES"
                        },
                        {
                            message: "ANC Visit #3",
                            formToOpen: "ANC_SERVICES"
                        }
                    ],
                    timelineEvents: [
                        {
                            title: "Event 1",
                            details: ["Detail 1", "Detail 2"],
                            status: "upcoming",
                            date: "1y 2m ago"
                        },
                        {
                            title: "Event 1",
                            details: ["Detail 1", "Detail 2"],
                            status: "overdue",
                            date: "1y 2m ago"
                        },
                        {
                            title: "Event 2",
                            details: ["Detail 3", "Detail 4"],
                            status: "done",
                            date: "2m 3d ago"
                        }
                    ]
                }
            );
        }
    }
}