
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
// 8- TESTING PROGRAM

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
        // console.log("ELEVATORS LIST:"); 
        // console.table(this.elevatorsList); 
        // console.log("BUTTONS UP LIST:"); 
        // console.table(this.buttonsUpList); 
        // console.log("BUTTONS DOWN LIST:"); 
        // console.table(this.buttonsDownList); 
    }
    
    createElevatorsList() {
        for (let i = 1; i <= this.numberOfElevators; i++) {
            this.elevatorsList.push(new Elevator(i, this.numberOfFloors, 1, elevatorStatus.IDLE, sensorStatus.OFF, sensorStatus.OFF));
            // console.log("CREATED ELEVATOR", i);
        }
    }    
    
    createButtonsUpList() {
        for (let i = 1; i < this.numberOfFloors; i++) {
                this.buttonsUpList.push(new Button(i, buttonStatus.OFF, i));
            // console.log("CREATED BUTTON UP", i);
        }
    }    
    
    createButtonsDownList() {
        for (let i = 2; i <= this.numberOfFloors; i++) {
            this.buttonsDownList.push(new Button(i, buttonStatus.OFF, i));
            // console.log("CREATED BUTTON DOWN", i);
        }
    } 
    
    /* ******* LOGIC TO FIND THE BEST ELEVATOR WITH A PRIORITIZATION LOGIC ******* */
    findElevator(currentFloor, direction) {
        let bestElevator;
        let activeElevatorList = [];
        let idleElevatorList = [];
        
        this.elevatorsList.forEach(elevator => {
            // console.log("ELEVATOR " + elevator.id + " STATUS: "+ elevator.status);
            if (elevator.status == direction && !element.status.IDLE) {
                if (elevator.status == elevatorStatus.UP && elevator.floor <= currentFloor || elevator.status == elevatorStatus.DOWN && elevator.floor >= currentFloor) {
                    activeElevatorList.push(elevator);
                }
            } else {
                idleElevatorList.push(elevator);
            }
        });
        
        if  (!activeElevatorList.length == 0) {
            bestElevator = this.findNearestElevator(currentFloor, activeElevatorList);
        } else {
            bestElevator = this.findNearestElevator(currentFloor, idleElevatorList);
        }

        return bestElevator;
    }

    /* ******* LOGIC TO FIND THE NEAREST ELEVATOR ******* */
    findNearestElevator(currentFloor, selectedList) {
        let bestElevator = selectedList[0];
        let bestDistance = Math.abs(selectedList[0].floor - currentFloor); //Math.abs() returns the absolute value of a number (always positive).
        selectedList.forEach(elevator => {
            if (Math.abs(elevator.floor - currentFloor) < bestDistance) {
                bestElevator = elevator;
            }
        });
        console.log("   >>> elevator " + bestElevator.id + " was called <<<");
        
        return bestElevator;
    }

    /* ******* REQUEST FOR AN ELEVATOR BY PRESSING THE UP OU DOWN BUTTON ******* */
    requestElevator(requestedFloor, direction) {
        if(direction == buttonDirection.UP) {
            this.buttonsUpList[requestedFloor-1].status = buttonStatus.ON;
            console.log("SOMEONE WANTS TO GO TO FLOOR:", requestedFloor);
            console.log("DIRECTIONAL BUTTON PRESSED:", direction);
        } else {
            this.buttonsUpList[requestedFloor-2].status = buttonStatus.OFF;
            console.log("SOMEONE WANTS TO GO TO FLOOR:", requestedFloor);
            console.log("DIRECTIONAL BUTTON PRESSED:", direction);
        }
       let bestElevator = this.findElevator(requestedFloor, direction);
        bestElevator.addFloorToFloorList(requestedFloor);
        bestElevator.moveElevator(requestedFloor);
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
        this.elevatorDisplay = new Display(0, displayStatus.ON, 0);
        this.floorDoorsList = [];
        this.floorDisplaysList = [];
        this.floorButtonsList = [];
        this.floorList = [];

        this.createFloorDoorsList();     
        this.createDisplaysList();  
        this.createFloorButtonsList();  

        // console.log("ELEVATOR DOOR");
        // console.log(this.elevatorDoor);
        // console.log("----------------------------------");
        // console.log("FLOOR DOORS LIST (elevator " + this.id + "):"); 
        // console.table(this.floorDoorsList); 
        // console.log("FLOOR BUTTONS LIST (elevator " + this.id + "):"); 
        // console.table(this.floorButtonsList); 

    }

    /* ******* CREATE A LIST WITH A DOOR OF EACH FLOOR ******* */
    createFloorDoorsList() {
        for (let i = 1; i <= this.numberOfFloors; i++) {
            this.floorDoorsList.push(new Door(i, doorStatus.CLOSED, i));
            // console.log("CREATED DOOR AT FLOOR", i);
        }
    }  
    /* ******* CREATE A LIST WITH A DISPLAY OF EACH FLOOR ******* */
    createDisplaysList() {
        for (let i = 1; i <= this.numberOfFloors; i++) {
            this.floorDisplaysList.push(new Display(i, displayStatus.ON, i));
            // console.log("CREATED DISPLAY AT FLOOR ", i);
        }
    }  

    /* ******* CREATE A LIST WITH A BUTTON OF EACH FLOOR ******* */
    createFloorButtonsList() {
        for (let i = 1; i <= this.numberOfFloors; i++) {
            this.floorButtonsList.push(new Button(i, buttonStatus.OFF, i));
            // console.log("CREATED BUTTON AT FLOOR ", i);
        }
    }  

    /* ******* LOGIC TO ADD A FLOOR TO THE FLOOR LIST ******* */
    addFloorToFloorList(floor) {
        this.floorList.push(floor);
        this.floorList.sort(function(a, b){return a-b});
    }

    /* ******* LOGIC TO MOVE ELEVATOR ******* */
    moveElevator(requestedFloor) {
        while (!this.floorList.length == 0) {
            if (this.status == elevatorStatus.IDLE) {
                if (this.floor < requestedFloor) {
                    this.status = elevatorStatus.UP;
                } else {
                    this.status = elevatorStatus.DOWN;
                }
            }
            if (this.status == elevatorStatus.UP) {
                this.moveUp();
            } else {
                // this.moveDown();
            }
        }
    }

    /* ******* LOGIC TO MOVE UP ******* */
    moveUp() {
        let tempArray = this.floorList;
        // this.updateDisplays(this.floor);
        for (let i = this.floor; i < tempArray[tempArray.length - 1]; i++) {
            console.log(`Moving elevator ${this.id} UP from floor ${i} to floor ${i + 1}`);
            let nextFloor = (i+1);
            this.floor = nextFloor;
            this.updateDisplays(this.floor);
            if(tempArray.includes(nextFloor)) {
                if (this.floorDoorsList[i].status.OPENED || this.elevatorDoor.status.OPENED) {
                    console.log("DOORS ARE OPEN, CLOSING DOORS BEFORE MOVE UP");
                    closeDoors();
                }
                this.deleteFloorFromList(nextFloor);
            }




        }
    }

    /* ******* LOGIC TO UPDATE DISPLAYS OF ELEVATOR AND SHOW FLOOR ******* */
    updateDisplays(stopFloor) {
        console.log(`   All displays of elevator ${this.id} show the floor number ${stopFloor}`);
    }

    /* ******* LOGIC TO DELETE ITEM FROM FLOORS LIST ******* */
    deleteFloorFromList(stopFloor) {
        let index = this.floorList.indexOf(stopFloor);
        if (index > -1) {
            this.floorList.splice(index, 1);
        }
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

function scenario1() {
    let columnScenario1 = new Column(1, columnStatus.ACTIVE, 10, 2); //parameters (id, columnStatus.ACTIVE/INACTIVE, numberOfFloors, numberOfElevators)
    console.log("SCENARIO 1:");
    columnScenario1.elevatorsList[0].floor = 2; //floor where the elevator is
    columnScenario1.elevatorsList[1].floor = 6; //floor where the elevator is
    
    columnScenario1.requestElevator(4, buttonDirection.UP); //parameters (requestedFloor, buttonDirection.UP/DOWN)
    // columnScenario1.requestFloor(7, elevator);
    console.log("==================================");
}

function scenario2() {
    let columnScenario2 = new Column(1, columnStatus.ACTIVE, 10, 2);     
    console.log("SCENARIO 2:");
    columnScenario2.elevatorsList[0].floor = 10;
    columnScenario2.elevatorsList[1].floor = 3;

    console.log("Person 1:");
    columnScenario2.requestElevator(1, buttonDirection.UP);
    // columnScenario2.requestFloor(6, elevator);
    console.log("----------------------------------");
    console.log("Person 2:");
    columnScenario2.requestElevator(3, buttonDirection.UP);
    // columnScenario2.requestFloor(5, elevator);
    console.log("----------------------------------");
    console.log("Person 3:");
    columnScenario2.requestElevator(9, buttonDirection.DOWN);
    // columnScenario2.requestFloor(2, elevator);
    console.log("==================================");
}

function scenario3() {
    let columnScenario3 = new Column(1, columnStatus.ACTIVE, 10, 2);     
    console.log("SCENARIO 3:");
    columnScenario3.elevatorsList[0].floor = 10;
    columnScenario3.elevatorsList[1].floor = 3;
    columnScenario3.elevatorsList[1].status.UP;
    columnScenario3.elevatorsList[1].moveElevator(6);

    console.log("Person 1:");
    columnScenario3.requestElevator(3, buttonDirection.DOWN);
    // columnScenario3.requestFloor(6, elevator);
    console.log("----------------------------------");
    console.log("Person 2:");
    columnScenario3.requestElevator(10, buttonDirection.DOWN);
    // columnScenario3.requestFloor(3, elevator);
    console.log("==================================");
}

scenario1();
// scenario2();
// scenario3();