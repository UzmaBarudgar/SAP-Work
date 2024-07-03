sap.ui.require(
    [
        'sap/fe/test/JourneyRunner',
        'employeedetails/test/integration/FirstJourney',
		'employeedetails/test/integration/pages/EmployeesList',
		'employeedetails/test/integration/pages/EmployeesObjectPage',
		'employeedetails/test/integration/pages/LeavesObjectPage'
    ],
    function(JourneyRunner, opaJourney, EmployeesList, EmployeesObjectPage, LeavesObjectPage) {
        'use strict';
        var JourneyRunner = new JourneyRunner({
            // start index.html in web folder
            launchUrl: sap.ui.require.toUrl('employeedetails') + '/index.html'
        });

       
        JourneyRunner.run(
            {
                pages: { 
					onTheEmployeesList: EmployeesList,
					onTheEmployeesObjectPage: EmployeesObjectPage,
					onTheLeavesObjectPage: LeavesObjectPage
                }
            },
            opaJourney.run
        );
    }
);