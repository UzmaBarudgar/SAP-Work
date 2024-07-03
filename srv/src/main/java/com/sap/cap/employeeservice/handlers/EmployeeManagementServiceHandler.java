package com.sap.cap.employeeservice.handlers;

import cds.gen.employeeservice.CalcSateWiseEmplContext;
import cds.gen.employeeservice.CalcSingleStateCountContext;
import cds.gen.employeeservice.EmployeeService_;
import cds.gen.employeeservice.Employees;
import cds.gen.employeeservice.Employees_;
import cds.gen.employeeservice.States;
import cds.gen.employeeservice.States_;
import cds.gen.employeeservice.ZeroStateCountContext;

import com.sap.cds.Result;
import com.sap.cds.ql.CQL;
import com.sap.cds.ql.Select;
import com.sap.cds.ql.Update;
import com.sap.cds.ql.cqn.CqnDelete;
import com.sap.cds.ql.cqn.CqnElementRef;
import com.sap.cds.ql.cqn.CqnSelect;
import com.sap.cds.services.EventContext;
import com.sap.cds.services.cds.CdsCreateEventContext;
import com.sap.cds.services.cds.CdsDeleteEventContext;
import com.sap.cds.services.cds.CdsUpdateEventContext;
import com.sap.cds.services.cds.CqnService;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.Before;
import com.sap.cds.services.handler.annotations.On;
import com.sap.cds.services.handler.annotations.ServiceName;
import com.sap.cds.services.persistence.PersistenceService;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@ServiceName(EmployeeService_.CDS_NAME)
public class EmployeeManagementServiceHandler implements EventHandler {

    @Autowired 
    PersistenceService db;

    @Before(event = CqnService.EVENT_CREATE, entity = Employees_.CDS_NAME)
    public void beforeCreateEmployee(Employees employees, EventContext context) {
        // Fetch the maximum employee number
        CqnElementRef number = CQL.get("number");
        Result result = db.run(Select.from(Employees_.class).columns(
            CQL.max(number).as("maxNumber")));
        Integer maxEmployeeNumber = (Integer) result.single().get("maxNumber");

        // Determine the new employee number
        employees.setNumber(maxEmployeeNumber + 1);
    }

    @On(event = CqnService.EVENT_CREATE, entity = Employees_.CDS_NAME)
    public void onCreateEmployee(Employees employees, CdsCreateEventContext context) {
        
        String event = context.getEvent();
        System.out.println("************Event name:"+ event);
        System.out.println("************Employeeobject:"+ employees.getName());
         // Check if the salary is greater than 10000
        if (employees.getSalary() != null && employees.getSalary().compareTo(new BigDecimal("10000")) > 0) {
            // Abort the transaction and set an error message
            throw new RuntimeException("Salary cannot be greater than 10000");
        }

            System.out.println("********Inside Create Event");
            System.out.println("***********State ID in State"+employees.getStateId());
             Result stateResult =  db.run(Select.from(States_.class).byId(employees.getStateId()));
             
             stateResult.first(States.class).ifPresent(
                state -> {
                    System.out.println("***********State ID in State"+state.getId());
                    state.setEmployeeCount(state.getEmployeeCount()+1);
                    db.run(Update.entity(States_.class).data(state));
                }
             );
        }  
        

    @On(event = CqnService.EVENT_UPDATE, entity = Employees_.CDS_NAME)
    public void onCreateEmployee(Employees employees, CdsUpdateEventContext context) {
    
        System.out.println("************Employeeobject:"+ employees.getName());
         // Check if the salary is greater than 10000

            CqnSelect fetchExistingEmp = Select.from(Employees_.class)
            .columns(e -> e.state_ID()).where(e -> e.ID().eq(employees.getId()));
            Optional<Employees> existingEmp = db.run(fetchExistingEmp).first(Employees.class);
            
            if(existingEmp.get().getStateId() !=  employees.getStateId()){
            Result oldStateResult =  db.run(Select.from(States_.class).byId(existingEmp.get().getStateId()));
            oldStateResult.first(States.class).ifPresent(
                oldState -> {
                    oldState.setEmployeeCount(oldState.getEmployeeCount()-1);
                      db.run(Update.entity(States_.class).data(oldState));
            });
            Result newStateResult =  db.run(Select.from(States_.class).byId(employees.getStateId()));
            newStateResult.first(States.class).ifPresent(
               state -> {
                   state.setEmployeeCount(state.getEmployeeCount()+1);
                   db.run(Update.entity(States_.class).data(state));
               }
            );
        }else {
            return;
        }
    }

    @On(event = CqnService.EVENT_DELETE, entity = Employees_.CDS_NAME)
    public void beforeDeleteEmployee(CdsDeleteEventContext context, Employees employee) {
         System.out.println("------------- Inside beforeDeleteEmployee method -------------");
 
        //Delete record
        CqnDelete selectEmp = context.getCqn();
        Select<?> select = Select.from(selectEmp.ref());
        selectEmp.where().ifPresent(select::where);
 
        // logger.info("selectEmp employee ID ****** " + select);
        Optional<Employees> deleteEmp = db.run(select).first(Employees.class);
        // logger.info("delete employee ID ****** "+deleteEmp.get().getId());
        System.out.println("Inside Delete Event");
        Result stateResult =  db.run(Select.from(States_.class).byId(deleteEmp.get().getStateId()));
        stateResult.first(States.class).ifPresent(
            state -> {
                state.setEmployeeCount(state.getEmployeeCount()-1);
                  db.run(Update.entity(States_.class).data(state));
        });
    }

    @On(event =CalcSateWiseEmplContext.CDS_NAME)
    public void reCalcEmployee( CalcSateWiseEmplContext context) {
        System.out.println("Inside Recalculate Employee count");
        // Fetch all states
        Result allStatesResult = db.run(Select.from(States_.class));
 
        for (Map<String, Object> state : allStatesResult) {
            String stateId = (String) state.get("ID");
             System.out.println("********************stateID "+stateId);
            Result employeeCountResult = db.run(Select.from(Employees_.class)
                    .columns("state_ID")
                    .where(e -> e.get("state_ID").eq(stateId)));
            long count = employeeCountResult.rowCount();
 
            db.run(Update.entity(States_.class).data(Collections.singletonMap("employeeCount", count))
                    .where(e -> e.get("ID").eq(stateId)));
 
             System.out.println("State: " + state.get("StateName") + ", Employee Count: "
             + count);
        }
         context.setCompleted();
    }

    @On(event =CalcSingleStateCountContext.CDS_NAME)
    public void reCalcSingleEmployee( CalcSingleStateCountContext context) {
      
        // Get the employee ID from the context
        Select<?> empSelect = toSelect(context.getCqn());
        Optional<Employees> countEmp = this.db.run(empSelect).first(Employees.class);
 
        // Step 1: Retrieve the employee to get the associated state ID
        Result employeeResult = db.run(Select.from(Employees_.class).where(e -> e.get("ID").eq(countEmp.get().getId())));
 
        if (employeeResult != null && !employeeResult.list().isEmpty()) {
            Optional<Employees> employeeOpt = employeeResult.first(Employees.class);
            if (employeeOpt.isPresent()) {
                Employees employee = employeeOpt.get();
 
                // Get the state ID from the employee
                String stateId = employee.getStateId();
 
                // Step 2: Retrieve the no of state using the state ID from employee table.
                Result stateResult = db
                        .run(Select.from(Employees_.class).where(s -> s.get("state_ID").eq(stateId)));
 
                // System.out.println("stateResult
                // ============="+stateResult+"______"+stateResult.rowCount());
                if (stateResult != null && !stateResult.list().isEmpty()) {
                    Optional<States> stateOpt = stateResult.first(States.class);
                    if (stateOpt.isPresent()) {
                        States state = stateOpt.get();
 
                        int count = (int) stateResult.rowCount();
 
                        db.run(Update.entity(States_.class)
                                .data(Collections.singletonMap("employeeCount", count))
                                .where(e -> e.get("ID").eq(stateId)));
 
                        String stateInfo = String.format("State[ID=%s, StateName=%s, EmployeeCount=%d]",
                                state.getId(), state.getState(), state.getEmployeeCount());
                        System.out.println("+++++++++++++++++++++==========  " + stateInfo);
                        // context.setResult(stateInfo);
                    } else {
                        // context.setResult("State not found");
                    }
                } else {
                    // context.setResult("State not found");
                }
            } else {
                // context.setResult("Employee not found");
            }
        } else {
            // context.setResult("Employee not found");
        }
        context.setCompleted();
    }

    @On(event = ZeroStateCountContext.CDS_NAME)
    public void zeroStateCountSingleRow(ZeroStateCountContext context) {
        // Get the employee ID from the context
        // String employeeId = context.getId();
 
        // Get the employee ID from the context
        Select<?> empSelect = toSelect(context.getCqn());
        Optional<Employees> deleteEmp = this.db.run(empSelect).first(Employees.class);
 
        // Step 1: Retrieve the employee to get the associated state ID
        Result employeeResult = db.run(Select.from(Employees_.class).where(e -> e.get("ID").eq(deleteEmp.get().getId())));
 
        if (employeeResult != null && !employeeResult.list().isEmpty()) {
            Optional<Employees> employeeOpt = employeeResult.first(Employees.class);
            if (employeeOpt.isPresent()) {
                Employees employee = employeeOpt.get();
 
                // Get the state ID from the employee
                String stateId = employee.getStateId();
 
                // Step 2: Retrieve the state using the state ID
                Result stateResult = db.run(Select.from(States_.class).where(s -> s.get("ID").eq(stateId)));
 
                if (stateResult != null && !stateResult.list().isEmpty()) {
                    Optional<States> stateOpt = stateResult.first(States.class);
                    if (stateOpt.isPresent()) {
                        States state = stateOpt.get();
 
                        // Reset the employee count to zero
                        state.setEmployeeCount(0);
                        db.run(Update.entity(States_.class).data(state).where(s -> s.get("ID").eq(stateId)));
 
                        String stateInfo = String.format("State[ID=%s, StateName=%s, EmployeeCount=%d]",
                                state.getId(), state.getState(), state.getEmployeeCount());
                        // context.setResult(stateInfo);
                    } else {
                        // context.setResult("State not found");
                    }
                } else {
                    // context.setResult("State not found");
                }
            } else {
                // context.setResult("Employee not found");
            }
        } else {
            // context.setResult("Employee not found");
        }
        context.setCompleted();
    }

    private static Select<?> toSelect(CqnSelect cqnSelect) {
        Select<?> select = Select.from(cqnSelect.ref());
        cqnSelect.where().ifPresent(select::where);
        return select;
        }
     

}
