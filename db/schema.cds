namespace com.sap.cap.employeemanagement;

entity Employees
{
    key ID : UUID ;
    number :  Integer
        @title : 'Employee Number';
    name : String
        @title : 'Name';
    lwd : Date
        @title : 'LWD';
    salary : Decimal(15,2)
        @mandatory
        @title : 'Salary';
    email : String
        @title : 'Email'
        @unique;
    nokPhone : String
        @title : 'NOK Phone';
    leaves :  Composition of many Leaves on leaves.employees = $self;
    city : Association to one Cities;
    state : Association to one States;
}

entity Leaves
{
    key ID : UUID;
    date : Date
        @mandatory
        @title : 'Date';
    days : Integer
        @mandatory
        @title : 'Days';
    employees : Association to one Employees;
}

entity Cities
{
    key ID : UUID;
    city : String(30)
        @title : 'City';
    states : Association to one States;
}

entity States
{
    key ID : UUID;
    state : String(30)
        @title : 'State';
    employeeCount :Integer
        @title : 'Employee Count';
    cities : Association to many Cities on cities.states = $self;
}