
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
        console.log("CREATED COLUMN", this.id);
        console.log("NUMBER OF FLOORS:", this.numberOfFloors);
        console.log("NUMBER OF ELEVATORS:", this.numberOfElevators);
        console.log("----------------------------------");

        this.createElevatorsList();    
        this.createButtonsUpList();     
        this.createButtonsDownList();     
        console.table("ELEVATORS LIST:"); 
        console.table(this.elevatorsList); 
        console.table("BUTTONS UP LIST:"); 
        console.table(this.buttonsUpList); 
        console.table("BUTTONS DOWN LIST:"); 
        console.table(this.buttonsDownList); 
    }
    
    createElevatorsList() {
        for (let i = 1; i <= this.numberOfElevators; i++) {
            this.elevatorsList.push(new Elevator(i, this.numberOfFloors, 1, elevatorStatus.IDLE, sensorStatus.OFF, sensorStatus.OFF, doorStatus.CLOSED));
            console.log("CREATED ELEVATOR", i);
            console.log("----------------------------------");
        }
    }    
    
    createButtonsUpList() {
        for (let i = 1; i < this.numberOfFloors; i++) {
                this.buttonsUpList.push(new Button(i, buttonStatus.OFF, i));
            console.log("CREATED BUTTON UP", i);
        }
        console.log("----------------------------------");
    }    
    
    createButtonsDownList() {
        for (let i = 2; i <= this.numberOfFloors; i++) {
            this.buttonsDownList.push(new Button(i, buttonStatus.OFF, i));
            console.log("CREATED BUTTON DOWN", i);
        }
        console.log("----------------------------------");
    }    
    
}

//------------------------------------------- ELEVATOR CLASS -----------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------------------------
class Elevator {
    constructor(id, numberOfFloors, floor, elevatorStatus, weightSensorStatus, obstructionSensorStatus, elevatorDoorStatus) {
        this.id = id;
        this.numberOfFloors = numberOfFloors;
        this.floor = floor;
        this.status = elevatorStatus;
        this.weightSensor = weightSensorStatus;
        this.obstructionSensor = obstructionSensorStatus;
        this.elevatorDoor = elevatorDoorStatus;
        this.floorDoorsList = [];
        this.floorButtonsList = [];
        this.floorList = [];

        this.createFloorDoorsList();     
        // console.table("FLOOR DOORS LIST:"); 
        // console.table(this.floorDoorsList); 

    }

    createFloorDoorsList() {
        for (let i = 1; i <= this.numberOfFloors; i++) {
            this.floorDoorsList.push(new Button(i, doorStatus.CLOSED, i));
            console.log("CREATED DOOR FLOOR", i);
        }
        console.log("----------------------------------");
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
// column1.requestElevator(3, "UP");
// column1.requestFloor(7, elevator);