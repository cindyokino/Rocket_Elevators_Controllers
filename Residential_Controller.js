
/** ********************************************** **
	@Author			Cindy Okino
	@Website		https://github.com/cindyokino
	@Last Update	October 2, 2020
    
    
// SUMMARY:
// 1- GLOBAL VARIABLES
// 2- COLUMN CLASS
// 3- ELEVATOR CLASS
// 4- DOOR CLASS
// 5- BUTTON CLASS
// 6- DISPLAY CLASS
// 7- ENUMS
// 8- LISTENERS 
// 9- TESTING PROGRAM

//CONTROLLED OBJECTS:
// Columns: controls a list of N elevators
// Elevators: controls doors, buttons, displays

** ************************************************* **/


//------------------------------------------- GLOBAL VARIABLES ---------------------------------------------------------------------
//----------------------------------------------------------------------------------------------------------------------------------
let numberOfColumns;
let numberOfFloors;    
let numberOfElevators;
let waitingTime;         //How many seconds the door remains open
let maxWeight;          //Maximum weight an elevator can carry in KG


//------------------------------------------- COLUMN CLASS -----------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------------------------
class Column {
    constructor(id, columnStatus, numberOfFloors, numberOfElevators) {
        this.id = id;
        this.status = columnStatus;
        this.numberOfFloors = numberOfFloors;
        this.numberOfElevators = numberOfElevators;
        this.elevatorsList = [];
        this.buttonsUpList = [];
        this.buttonsDownList = [];

        this.createElevatorsList();
        this.createButtonsUpList();     
        this.createButtonsDownList();     

        console.log("CREATED COLUMN", this.id);
        console.log("NUMBER OF FLOORS:", this.numberOfFloors);
        console.log("NUMBER OF ELEVATORS:", this.numberOfElevators);
        console.log("----------------------------------");
        console.log("ELEVATORS LIST:"); 
        console.table(this.elevatorsList); 
        // console.log("BUTTONS UP LIST:"); 
        // console.table(this.buttonsUpList); 
        // console.log("BUTTONS DOWN LIST:"); 
        // console.table(this.buttonsDownList); 
    }
    
    createElevatorsList() {
        for (let i = 1; i <= this.numberOfElevators; i++) {
            this.elevatorsList.push(new Elevator(i, this.numberOfFloors, 1, elevatorStatus.IDLE, sensorStatus.OFF, sensorStatus.OFF));
            // console.log("CREATED ELEVATOR", i);
            // console.log("----------------------------------");
        }
    }    
    
    createButtonsUpList() {
        for (let i = 1; i < this.numberOfFloors; i++) {
                this.buttonsUpList.push(new Button(i, buttonStatus.OFF, i));
            // console.log("CREATED BUTTON UP", i);
        }
        // console.log("----------------------------------");
    }    
    
    createButtonsDownList() {
        for (let i = 2; i <= this.numberOfFloors; i++) {
            this.buttonsDownList.push(new Button(i, buttonStatus.OFF, i));
            // console.log("CREATED BUTTON DOWN", i);
        }
        // console.log("----------------------------------");
    } 
    
    /* ******* LOGIC TO FIND THE BEST ELEVATOR WITH A PRIORITIZATION LOGIC ******* */
    findElevator(currentFloor, direction) {
        let activeElevatorList = [];
        let idleElevatorList = [];
        
        this.elevatorsList.forEach(elevator => {
            console.log("ELEVATOR " + elevator.id + " STATUS: "+ elevator.status);
            if (elevator.status == direction && !element.status.IDLE) {
                if (elevator.status == elevatorStatus.UP && elevator.floor <= currentFloor || elevator.status == elevatorStatus.DOWN && elevator.floor >= currentFloor) {
                    activeElevatorList.push(elevator);
                }
            } else {
                idleElevatorList.push(elevator);
            }
        });

        if  (!activeElevatorList.length == 0) {
            this.findNearestElevator(currentFloor, activeElevatorList);
        } else {
            this.findNearestElevator(currentFloor, idleElevatorList);
        }

        // return bestElevator;
    }

    /* ******* REQUEST FOR AN ELEVATOR ******* */
    findNearestElevator(currentFloor, selectedList) {
        let bestElevator = selectedList[0];
        let bestDistance = Math.abs(selectedList[0].floor - currentFloor); //Math.abs() returns the absolute value of a number (always positive).
        selectedList.forEach(item => {
            
        });
    }

    /* ******* REQUEST FOR AN ELEVATOR ******* */
    requestElevator(requestedFloor, direction) {
        if(direction == buttonDirection.UP) {
            this.buttonsUpList[requestedFloor-1].status = buttonStatus.ON;
            console.log("REQUESTED FLOOR:", requestedFloor);
            console.log("BUTTON DIRECTION:", direction);
            console.log("----------------------------------");
    }
        else {
            this.buttonsUpList[requestedFloor-2].status = buttonStatus.OFF;
        }
        let bestElevator = this.findElevator(requestedFloor, direction);
        // addFloorToFloorList(bestElevator, currentFloor);
        // moveElevator(bestElevator, floorList, currentFloor);
    }
    
}


//------------------------------------------- ELEVATOR CLASS -----------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------------------------
class Elevator {
    constructor(id, numberOfFloors, floor, elevatorStatus, weightSensorStatus, obstructionSensorStatus) {
        this.id = id;
        this.numberOfFloors = numberOfFloors;
        this.floor = floor;
        this.status = elevatorStatus;
        this.weightSensor = weightSensorStatus;
        this.obstructionSensor = obstructionSensorStatus;
        this.elevatorDoor = new Door(0, doorStatus.CLOSED, 0);
        this.floorDoorsList = [];
        this.floorButtonsList = [];
        this.floorList = [];

        this.createFloorDoorsList();     
        this.createFloorButtonsList();  

        // console.log("ELEVATOR DOOR");
        // console.log(this.elevatorDoor);
        // console.log("----------------------------------");
        // console.log("FLOOR DOORS LIST (elevator " + this.id + "):"); 
        // console.table(this.floorDoorsList); 
        // console.log("FLOOR BUTTONS LIST (elevator " + this.id + "):"); 
        // console.table(this.floorButtonsList); 

    }

    createFloorDoorsList() {
        for (let i = 1; i <= this.numberOfFloors; i++) {
            this.floorDoorsList.push(new Door(i, doorStatus.CLOSED, i));
            // console.log("CREATED DOOR AT FLOOR", i);
        }
        // console.log("----------------------------------");
    }  

    createFloorButtonsList() {
        for (let i = 1; i <= this.numberOfFloors; i++) {
            this.floorButtonsList.push(new Button(i, buttonStatus.OFF, i));
            // console.log("CREATED FLOOR BUTTON", i);
        }
        // console.log("----------------------------------");
    }  

}


//------------------------------------------- DOOR CLASS -----------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------------------------
class Door {
    constructor(id, doorStatus, floor) {
        this.id = id;
        this.status = doorStatus;
        this.floor = floor;
    }
}


//------------------------------------------- BUTTON CLASS -----------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------------------------
class Button {
    constructor(id, buttonStatus, floor) {
        this.id = id;
        this.status = buttonStatus;
        this.floor = floor;
    }
}


//------------------------------------------- DISPLAY CLASS -----------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------------------------
class Display {
    constructor(id, displayStatus, floor) {
        this.id = id;
        this.status = displayStatus;
        this.floor = floor;
    }
}


//------------------------------------------- ENUMS -----------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------------------------
/* ******* COLUMN STATUS ******* */
const columnStatus = {
    ACTIVE: 'active',
    INACTIVE: 'inactive'
};

/* ******* ELEVATOR STATUS ******* */
const elevatorStatus = {
    IDLE: 'idle',
    UP: 'up',
    DOWN: 'down'
};

/* ******* BUTTON DIRECTION ******* */
const buttonDirection = {
    UP: 'up',
    DOWN: 'down'
};

/* ******* BUTTONS STATUS ******* */
const buttonStatus = {
    ON: 'on',
    OFF: 'off'
};

/* ******* SENSORS STATUS ******* */
const sensorStatus = {
    ON: 'on',
    OFF: 'off'
};

/* ******* DOORS STATUS ******* */
const doorStatus = {
    OPENED: 'opened',
    CLOSED: 'closed'
};

/* ******* DISPLAY STATUS ******* */
const displayStatus = {
    ON: 'on',
    OFF: 'off'
};


//------------------------------------------- TESTING PROGRAM ---------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------------------------

let column1 = new Column(1, columnStatus.ACTIVE, 10, 2);
column1.requestElevator(3, buttonDirection.UP);
// column1.requestFloor(7, elevator);