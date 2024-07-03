using { com.sap.cap.employeemanagement as db } from '../db/schema';

// Define Employee Service
service EmployeeService {
    @odata.draft.enabled
    entity Employees as projection on db.Employees
    actions {
        action calcSateWiseEmpl();
        action calcSingleStateCount();
        action zeroStateCount();
    };
    entity Leaves as projection on db.Leaves;
    entity Cities as projection on db.Cities;
    entity States as projection on db.States;
}