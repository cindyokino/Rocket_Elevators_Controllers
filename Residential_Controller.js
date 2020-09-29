
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
// 6- ENUMS
// 7- LISTENERS 
// 8- TESTING PROGRAM

//CONTROLED OBJECTS:
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

        this.createElevatorsList();    
        console.table(this.elevatorsList); 
        // this.createElevatorsList(numberOfElevators);     
        // this.createElevatorsList(numberOfElevators);     
    }
    
    createElevatorsList() {
        for (let i = 1; i <= this.numberOfElevators; i++) {
            this.elevatorsList.push(new Elevator(i, this.numberOfFloors, 1, elevatorStatus.IDLE, sensorStatus.OFF, sensorStatus.OFF, doorStatus.CLOSED));
            console.log("CREATED ELEVATOR", i);
        }
    }    
    
}


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
    }

}


//------------------------------------------- ENUMS -----------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------------------------
/* ******* COLUMN STATUS ******* */
const columnStatus = {
    ACTIVE: 'active',
    INNACTIVE: 'inactive'
};

const elevatorStatus = {
    IDLE: 'idle',
    UP: 'up',
    DOWN: 'down'
};

const buttonStatus = {
    ON: 'on',
    OFF: 'off'
};

const sensorStatus = {
    ON: 'on',
    OFF: 'off'
};

const doorStatus = {
    OPENED: 'opened',
    CLOSED: 'closed'
};

//------------------------------------------- TESTING PROGRAM ---------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------------------------

let column1 = new Column(1, columnStatus.ACTIVE, 10, 2);
// column1.requestElevator(3, "UP");
// column1.requestFloor(7, elevator);