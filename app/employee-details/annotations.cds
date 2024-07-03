using EmployeeService as service from '../../srv/employees-service';
annotate service.Employees with @(
    UI.FieldGroup #GeneratedGroup : {
        $Type : 'UI.FieldGroupType',
        Data : [
            {
                $Type : 'UI.DataField',
                Value : number,
            },
            {
                $Type : 'UI.DataField',
                Value : name,
            },
            {
                $Type : 'UI.DataField',
                Value : lwd,
            },
            {
                $Type : 'UI.DataField',
                Value : email,
            },
            {
                $Type : 'UI.DataField',
                Value : nokPhone,
            },
            {
                $Type : 'UI.DataField',
                Value : state_ID,
            },
            {
                $Type : 'UI.DataField',
                Value : city_ID,
            },
            {
                $Type : 'UI.DataField',
                Value : salary,
            },
            {
                $Type : 'UI.DataFieldForAction',
                Action : 'EmployeeService.calcSateWiseEmpl',
                Label : 'CalculateEmplCount',
            },
        ],
    },
    UI.Facets : [
        {
            $Type : 'UI.ReferenceFacet',
            ID : 'GeneratedFacet1',
            Label : 'General Information',
            Target : '@UI.FieldGroup#GeneratedGroup',
        },
        {
            $Type : 'UI.ReferenceFacet',
            Label : 'Leaves',
            ID : 'Leaves',
            Target : 'leaves/@UI.LineItem#Leaves1',
        },
    ],
    UI.LineItem : [
        {
            $Type : 'UI.DataField',
            Value : number,
        },
        {
            $Type : 'UI.DataField',
            Value : name,
        },
        {
            $Type : 'UI.DataField',
            Value : lwd,
        },
        {
            $Type : 'UI.DataField',
            Value : salary,
        },
        {
            $Type : 'UI.DataField',
            Value : email,
        },
        {
            $Type : 'UI.DataField',
            Value : state_ID,
        },
        {
            $Type : 'UI.DataField',
            Value : city_ID,
        },
        {
            $Type : 'UI.DataFieldForAction',
            Action : 'EmployeeService.recalcEmployee',
            Label : 'Calculate',
        },
        {
            $Type : 'UI.DataFieldForAction',
            Action : 'EmployeeService.zeroStateCount',
            Label : 'ZeroStateCount',
            Inline : true,
        },
        {
            $Type : 'UI.DataFieldForAction',
            Action : 'EmployeeService.calcSingleStateCount',
            Label : 'CalculateEmployee',
        },
    ],
);

annotate service.Employees with {
    city @Common.ValueList : {
        $Type : 'Common.ValueListType',
        CollectionPath : 'Cities',
        Parameters : [
                {
                    $Type : 'Common.ValueListParameterInOut',
                    LocalDataProperty : city_ID,
                    ValueListProperty : 'ID',
                },
                {
                    $Type : 'Common.ValueListParameterIn',
                    ValueListProperty : 'states_ID',
                    LocalDataProperty : state_ID,
                },
            ],
        Label : 'City',
    }
};

annotate service.Employees with {
    state @Common.ValueList : {
        $Type : 'Common.ValueListType',
        CollectionPath : 'States',
        Parameters : [
            {
                $Type : 'Common.ValueListParameterInOut',
                LocalDataProperty : state_ID,
                ValueListProperty : 'ID',
            },
            {
                $Type : 'Common.ValueListParameterDisplayOnly',
                ValueListProperty : 'state',
            },
        ],
        Label : 'State',
    }
};

annotate service.Employees with @(
    UI.SelectionFields : [
        state_ID,
        city_ID,
    ]
);
annotate service.Employees with {
    state @Common.Label : 'State'
};
annotate service.Employees with {
    city @Common.Label : 'City'
};
annotate service.Employees with {
    state @Common.Text : {
            $value : state.state,
            ![@UI.TextArrangement] : #TextOnly,
        }
};
annotate service.Employees with {
    state @Common.ValueListWithFixedValues : true
};
annotate service.States with {
    ID @Common.Text : {
        $value : state,
        ![@UI.TextArrangement] : #TextOnly,
    }
};
annotate service.Employees with {
    city @Common.Text : {
            $value : city.city,
            ![@UI.TextArrangement] : #TextOnly,
        }
};
annotate service.Employees with {
    city @Common.ValueListWithFixedValues : true
};
annotate service.Cities with {
    ID @Common.Text : {
        $value : city,
        ![@UI.TextArrangement] : #TextOnly,
    }
};
annotate service.Leaves with @(
    UI.Facets : [
        {
            $Type : 'UI.ReferenceFacet',
            Label : 'Leaves',
            ID : 'Leaves',
            Target : '@UI.FieldGroup#Leaves',
        },
    ],
    UI.FieldGroup #Leaves : {
        $Type : 'UI.FieldGroupType',
        Data : [
            {
                $Type : 'UI.DataField',
                Value : date,
            },{
                $Type : 'UI.DataField',
                Value : days,
            },],
    }
);
annotate service.Leaves with @(
    UI.LineItem #Leaves : [
    ]
);
annotate service.Leaves with @(
    UI.LineItem #Leaves1 : [
        {
            $Type : 'UI.DataField',
            Value : date,
        },{
            $Type : 'UI.DataField',
            Value : days,
        },]
);
annotate service.Employees with {
    number @Common.FieldControl : #ReadOnly
};
