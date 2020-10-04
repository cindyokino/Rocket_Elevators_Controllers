/* *********************************************** **
 @Author			Cindy Okino
 @Website		https://github.com/cindyokino
 @Last Update	October 9, 2020


 SUMMARY:
 **************************************************************************************
 **************************************************************************************
 **************************************************************************************
 1- COLUMN CLASS
    1a- Constructor and its attributes
    1b- Methods to create a list: createElevatorsList, createButtonsUpList, createButtonsDownList
    1c- Methods for logic: findElevator, findNearestElevator
    1d- Entry method: requestElevator
 2- ELEVATOR CLASS
    2a- Constructor and its attributes
    2b- Methods to create a list: createFloorDoorsList, createDisplaysList, createFloorButtonsList, addFloorToFloorList
    2c- Methods for logic: moveElevator, moveUp, moveDown, updateDisplays, openDoors, closeDoors, checkWeight, checkObstruction, deleteFloorFromList
    2d- Entry method: requestFloor
 3- DOOR CLASS
 4- BUTTON CLASS
 5- DISPLAY CLASS
 6- ENUMS
 7- GLOBAL VARIABLES
 8- TESTING PROGRAM
 9- TEST YOUR SCENARIO

 CONTROLLED OBJECTS:
 Columns: controls a list of N elevators
 Elevators: controls doors, buttons, displays

  ** ************************************************** */


import java.util.*;
import java.util.concurrent.TimeUnit;

//------------------------------------------- COLUMN CLASS -----------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------------------------
class Column {
    int id;
    ColumnStatus status;
    int numberOfFloors;
    int numberOfElevators;
    List<Elevator> elevatorsList;
    List<Button> buttonsUpList;
    List<Button> buttonsDownList;

    //----------------- Constructor and its attributes -----------------//
    public Column(int id, ColumnStatus columnStatus, int numberOfFloors, int numberOfElevators) {
        this.id = id;
        this.status = columnStatus;
        this.numberOfFloors = numberOfFloors;
        this.numberOfElevators = numberOfElevators;
        this.elevatorsList = new ArrayList<>();
        this.buttonsUpList = new ArrayList<>();
        this.buttonsDownList = new ArrayList<>();

        this.createElevatorsList();
        this.createButtonsUpList();
        this.createButtonsDownList();

        System.out.println("Created column" + this.id);
        System.out.println("Number of floors:" + this.numberOfFloors);
        System.out.println("Number of elevators:" + this.numberOfElevators);
        System.out.println("----------------------------------");
        // console.log("ELEVATORS LIST:");
        // console.table(this.elevatorsList);
    }


    //----------------- Methods to create a list -----------------//
    /* ******* CREATE A LIST OF ELEVATORS FOR THE COLUMN ******* */
    public void createElevatorsList() {
        for (int i = 1; i <= this.numberOfElevators; i++) {
            this.elevatorsList.add(new Elevator(i, this.numberOfFloors, 1, ElevatorStatus.IDLE, SensorStatus.OFF, SensorStatus.OFF));
        }
    }

    /* ******* CREATE A LIST WITH UP BUTTONS FROM THE FIRST FLOOR TO THE LAST LAST BUT ONE FLOOR ******* */
    public void createButtonsUpList() {
        for (int i = 1; i < this.numberOfFloors; i++) {
            this.buttonsUpList.add(new Button(i, ButtonStatus.OFF, i));
        }
    }

    /* ******* CREATE A LIST WITH DOWN BUTTONS FROM THE SECOND FLOOR TO THE LAST FLOOR ******* */
    public void createButtonsDownList() {
        for (int i = 2; i <= this.numberOfFloors; i++) {
            this.buttonsDownList.add(new Button(i, ButtonStatus.OFF, i));
        }
    }


    //----------------- Methods for logic -----------------//
    /* ******* LOGIC TO FIND THE BEST ELEVATOR WITH A PRIORITIZATION LOGIC ******* */
    public Elevator findElevator(int currentFloor, Direction direction) {
        Elevator bestElevator;
        List<Elevator> activeElevatorList = new ArrayList<>();
        List<Elevator> idleElevatorList = new ArrayList<>();
        List<Elevator> sameDirectionElevatorList = new ArrayList<>();
        this.elevatorsList.forEach(elevator -> {
            if (elevator.status != ElevatorStatus.IDLE) {
                //verify if the request is on the elevator way
                if (elevator.status == ElevatorStatus.UP && elevator.floor <= currentFloor || elevator.status == ElevatorStatus.DOWN && elevator.floor >= currentFloor) {
                    activeElevatorList.add(elevator);
                }
            } else {
                idleElevatorList.add(elevator);
            }
        });

        if (activeElevatorList.size() > 0) { //Create new list for elevators with same direction that the request
            sameDirectionElevatorList = activeElevatorList.stream().filter(elevator -> elevator.status.name() == direction.name());
        }

        if (sameDirectionElevatorList.size() > 0) {
            bestElevator = this.findNearestElevator(currentFloor, sameDirectionElevatorList);
        } else {
            bestElevator = this.findNearestElevator(currentFloor, idleElevatorList);
        }

        return bestElevator;
    }

    /* ******* LOGIC TO FIND THE NEAREST ELEVATOR ******* */
    public Elevator findNearestElevator(int currentFloor, List<Elevator> selectedList) {
        Elevator bestElevator = selectedList.get(0);
        int bestDistance = Math.abs(selectedList.get(0).floor - currentFloor); //Math.abs() returns the absolute value of a number (always positive).
        for (Elevator elevator : selectedList) {
            if (Math.abs(elevator.floor - currentFloor) < bestDistance) {
                bestElevator = elevator;
            }
        }
        System.out.println();
        System.out.println("   >> >>> ELEVATOR " + bestElevator.id + " WAS CALLED <<< <<");

        return bestElevator;
    }


    //----------------- Entry method -----------------//
    /* ******* ENTRY METHOD ******* */
    /* ******* REQUEST FOR AN ELEVATOR BY PRESSING THE UP OU DOWN BUTTON OUTSIDE THE ELEVATOR ******* */
    public void requestElevator(int requestedFloor, Direction direction) {
        if (direction == ButtonDirection.UP) {
            this.buttonsUpList.get(requestedFloor - 1).status = ButtonStatus.ON;
        } else {
            this.buttonsDownList.get(requestedFloor - 2).status = ButtonStatus.ON;
        }
        System.out.println(">> Someone request an elevator from floor <" + requestedFloor + "> and direction <" + direction + "> <<");
        this.elevatorsList.forEach(element -> {
            System.out.println("Elevator" + element.id + " | Floor:" + element.floor + " | Status:" + element.status);
        });
        Elevator bestElevator = this.findElevator(requestedFloor, direction);
        bestElevator.addFloorToFloorList(requestedFloor);
        bestElevator.moveElevator(requestedFloor, this);
    }
}


//------------------------------------------- ELEVATOR CLASS -----------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------------------------
class Elevator {
    int id;
    int numberOfFloors;
    int floor;
    ElevatorStatus elevatorStatus;
    WeightSensorStatus weightSensorStatus;
    ObstructionSensorStatus obstructionSensorStatus;
    Door elevatorDoor;
    Display elevatorDisplay;
    List<Door> floorDoorsList;
    List<Display> floorDisplaysList;
    List<Button> floorButtonsList;
    List<Integer> floorList;

    //----------------- Constructor and its attributes -----------------//
    public Elevator (int id, int numberOfFloors, int floor, ElevatorStatus elevatorStatus, WeightSensorStatus weightSensorStatus, ObstructionSensorStatus obstructionSensorStatus) {
        this.id = id;
        this.numberOfFloors = numberOfFloors;
        this.floor = floor;
        this.status = elevatorStatus;
        this.weightSensor = weightSensorStatus;
        this.obstructionSensor = obstructionSensorStatus;
        this.elevatorDoor = new Door(0, DoorStatus.CLOSED, 0);
        this.elevatorDisplay = new Display(0, DisplayStatus.ON, 0);
        this.floorDoorsList = new ArrayList<>();
        this.floorDisplaysList = new ArrayList<>();
        this.floorButtonsList = new ArrayList<>();
        this.floorList = new ArrayList<>();

        this.createFloorDoorsList();
        this.createDisplaysList();
        this.createFloorButtonsList();
    }


    //----------------- Methods to create a list -----------------//
    /* ******* CREATE A LIST WITH A DOOR OF EACH FLOOR ******* */
    public void createFloorDoorsList() {
        for (int i = 1; i <= this.numberOfFloors; i++) {
            this.floorDoorsList.add(new Door(i, DoorStatus.CLOSED, i));
        }
    }

    /* ******* CREATE A LIST WITH A DISPLAY OF EACH FLOOR ******* */
    public void createDisplaysList() {
        for (int i = 1; i <= this.numberOfFloors; i++) {
            this.floorDisplaysList.add(new Display(i, DisplayStatus.ON, i));
        }
    }

    /* ******* CREATE A LIST WITH A BUTTON OF EACH FLOOR ******* */
    public void createFloorButtonsList() {
        for (int i = 1; i <= this.numberOfFloors; i++) {
            this.floorButtonsList.add(new Button(i, ButtonStatus.OFF, i));
        }
    }


    //----------------- Methods for logic -----------------//
    /* ******* LOGIC TO MOVE ELEVATOR ******* */
    public void moveElevator(int requestedFloor, int requestedColumn) {
        while (this.floorList.size() != 0) {
            if (this.status == ElevatorStatus.IDLE) {
                if (this.floor < requestedFloor) {
                    this.status = ElevatorStatus.UP;
                } else if (this.floor == requestedFloor) {
                    this.openDoors();
                    this.deleteFloorFromList(requestedFloor);
                    requestedColumn.buttonsUpList.get(requestedFloor - 1).status = ButtonStatus.OFF;
                    requestedColumn.buttonsDownList.get(requestedFloor - 1).status = ButtonStatus.OFF;
                    this.floorButtonsList.get(requestedFloor - 1).status = ButtonStatus.OFF;
                } else {
                    this.status = ElevatorStatus.DOWN;
                }
            }
            if (this.status == ElevatorStatus.UP) {
                this.moveUp(requestedColumn);
            } else {
                this.moveDown(requestedColumn);
            }
        }
    }

    /* ******* LOGIC TO MOVE UP ******* */
    public void moveUp(int requestedColumn) {
        List<Integer> tempArray = this.floorList;
        for (int i = this.floor; i < tempArray.get(tempArray.size() - 1); i++) {
            if (this.floorDoorsList.get(i).status == DoorStatus.OPENED || this.elevatorDoor.status == DoorStatus.OPENED) {
                System.out.println("   Doors are open, closing doors before move up");
                this.closeDoors();
            }
            console.log("Moving elevator" + this.id + " <up> from floor " + i + " to floor " + i + 1);
            int nextFloor = (i + 1);
            this.floor = nextFloor;
            this.updateDisplays(this.floor);

            if (tempArray.contains(nextFloor)) {
                this.openDoors(int waitingTime);
                this.deleteFloorFromList(nextFloor);
                requestedColumn.buttonsUpList.get(i - 1).status = ButtonStatus.OFF;
                this.floorButtonsList.get(i).status = ButtonStatus.OFF;
            }
        }
        if (this.floorList.size() == 0) {
            this.status = ElevatorStatus.IDLE;
//            System.out.println("       Elevator"+ this.id + " is now " + this.status);
        } else {
            this.status = elevatorStatus.DOWN;
            console.log("       Elevator" + this.id + " is now going " + this.status);
        }
    }

    /* ******* LOGIC TO MOVE DOWN ******* */
    public void moveDown(int requestedColumn) {
        List<Integer> tempArray = this.floorList;
        for (int i = this.floor; i > tempArray.get(tempArray.size() - 1); i--) {
            if (this.floorDoorsList.get(i - 1).status == DoorStatus.OPENED || this.ElevatorDoor.status == DoorStatus.OPENED) {
                System.out.println("       Doors are open, closing doors before move down");
                this.closeDoors();
            }
            System.out.println("Moving elevator" + this.id + " <down> from floor " + i + " to floor " + i - 1);
            int nextFloor = (i - 1);
            this.floor = nextFloor;
            this.updateDisplays(this.floor);

            if (tempArray.contains(nextFloor)) {
                this.openDoors(int waitingTime);
                this.deleteFloorFromList(nextFloor);
                requestedColumn.buttonsDownList.get(i - 2).status = ButtonStatus.OFF;
                this.floorButtonsList.get(i - 1).status = ButtonStatus.OFF;
            }
        }
        if (this.floorList.size() == 0) {
            this.status = ElevatorStatus.IDLE;
//            System.out.println("       Elevator" + this.id + " is now " + this.status);
        } else {
            this.status = ElevatorStatus.UP;
            System.out.println("       Elevator" + this.id + " is now going " + this.status);
        }
    }

    /* ******* LOGIC TO UPDATE DISPLAYS OF ELEVATOR AND SHOW FLOOR ******* */
    public void updateDisplays(int elevatorFloor) {
        this.floorDisplaysList.forEach(display -> {
                display.floor = elevatorFloor;
        });
        System.out.println("Displays show #" + elevatorFloor);
    }

    /* ******* LOGIC TO OPEN DOORS ******* */
    public void openDoors(int waitingTime) {
        System.out.println("       Opening doors...");
        System.out.println("      Elevator" + this.id + " doors are opened");

        timer = new Timer();
        timer.schedule(new RemindTask(), waitingTime, TimeUnit.SECONDS);

        class RemindTask extends TimerTask {
            public void run() {
                while (this.weightSensor == SensorStatus.ON || this.obstructionSensor == SensorStatus.ON) {
                    this.elevatorDoor.status = DoorStatus.OPENED;
                    this.floorDoorsList[this.floor - 1].status = DoorStatus.OPENED;
                }
                timer.cancel(); //Terminate the timer thread
            }
        }
        this.closeDoors();
    }

    //**************************************************************************************************************
//        System.out.println("       Opening doors...");
//        System.out.println("      Elevator" + this.id + " doors are opened");
//
//        new java.util.Timer().schedule(
//            new java.util.TimerTask() {
//                public void run() {
//                    while (this.weightSensor == SensorStatus.ON || this.obstructionSensor == SensorStatus.ON) {
//                    this.elevatorDoor.status = DoorStatus.OPENED;
//                    this.floorDoorsList[this.floor - 1].status = DoorStatus.OPENED;
//                    }
//                }
//            }, waitingTime*1000
//        );
//        this.closeDoors();
//    }
    //**************************************************************************************************************

    //**************************************************************************************************************
//        int threeSecondsFromNow = new Date();
//        threeSecondsFromNow.setSeconds(threeSecondsFromNow.getSeconds() + 1);
//        console.log("       Opening doors...");
//        console.log(`       Elevator$ {this.id} doors are opened`);
//        while (new Date() < threeSecondsFromNow || this.weightSensor == sensorStatus.ON || this.obstructionSensor == sensorStatus.ON) {
//            this.elevatorDoor.status = doorStatus.OPENED;
//            this.floorDoorsList[this.floor - 1].status = doorStatus.OPENED;
//        }
//        this.closeDoors();
//    }
    //**************************************************************************************************************

    /* ******* LOGIC TO CLOSE DOORS ******* */
    public void closeDoors() {
        if (this.weightSensor == SensorStatus.OFF && this.ObstructionSensor == SensorStatus.OFF) {
            System.out.println("       Closing doors...");
            System.out.println("       Elevator" + this.id + " doors are closed");
            this.floorDoorsList.get(this.floor - 1).status = DoorStatus.CLOSED;
            this.elevatorDoor.status = DoorStatus.CLOSED;
        }
    }

    /* ******* LOGIC FOR WEIGHT SENSOR ******* */
    public void checkWeight(int maxWeight) {
        Random random = new Random();
        int randomWeight = random.nextInt(maxWeight+100); //This random simulates the weight from a weight sensor
        while (randomWeight > maxWeight) {
            this.weightSensor = SensorStatus.ON;
            System.out.println("       ! Elevator capacity reached, waiting until the weight is lower before continue...");
            randomWeight -= 100; //I'm supposing the random number is 600, I'll subtract 101 so it will be less than 500 (the max weight I proposed) for the second time it runs
        }
        this.weightSensor = SensorStatus.OFF;
        System.out.println("       Elevator capacity is OK");
    }

    /* ******* LOGIC FOR OBSTRUCTION SENSOR ******* */
    public void checkObstruction() {
        int probabilityNotBlocked = 70;
        Random random = new Random();
        int number = random.nextInt(100); //This random simulates the probability of an obstruction (I supposed 30% of chance something is blocking the door)
        while (number > probabilityNotBlocked) {
            this.obstructionSensor = SensorStatus.ON;
            System.out.println("       ! Elevator door is blocked by something, waiting until door is free before continue...");
            number -= 30; //I'm supposing the random number is 100, I'll subtract 30 so it will be less than 70 (30% probability), so the second time it runs theres no one blocking the door
        }
        this.obstructionSensor = SensorStatus.OFF;
        System.out.println("       Elevator door is FREE");
    }

    /* ******* LOGIC TO ADD A FLOOR TO THE FLOOR LIST ******* */
    addFloorToFloorList(floor) {
        this.floorList.push(floor);
        this.floorList.sort(function(a, b) {
            return a - b
        });
    }

    /* ******* LOGIC TO DELETE ITEM FROM FLOORS LIST ******* */
    deleteFloorFromList(stopFloor) {
        let index = this.floorList.indexOf(stopFloor);
        if (index > -1) {
            this.floorList.splice(index, 1);
        }
    }


    //----------------- Entry method -----------------//
    /* ******* ENTRY METHOD ******* */
    /* ******* REQUEST FOR A FLOOR BY PRESSING THE FLOOR BUTTON INSIDE THE ELEVATOR ******* */
    requestFloor(requestedFloor, requestedColumn) {
        console.log();
        console.log(` >> Someone inside the elevator$ {
            this.id
        } wants to go to floor<$ {
            requestedFloor
        }> <<`);
        this.checkWeight(maxWeight);
        this.checkObstruction();
        this.addFloorToFloorList(requestedFloor);
        this.moveElevator(requestedFloor, requestedColumn);
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
enum ColumnStatus {
    ACTIVE,
    INACTIVE
};

/* ******* ELEVATOR STATUS ******* */
enum ElevatorStatus {
    IDLE,
    UP,
    DOWN
};

/* ******* BUTTON DIRECTION ******* */
enum ButtonDirection {
    UP,
    DOWN
};

/* ******* BUTTONS STATUS ******* */
enum ButtonStatus {
    ON,
    OFF
};

/* ******* SENSORS STATUS ******* */
enum SensorStatus {
    ON,
    OFF
};

/* ******* DOORS STATUS ******* */
enum DoorStatus {
    OPENED,
    CLOSED
};

/* ******* DISPLAY STATUS ******* */
enum DisplayStatus {
    ON,
    OFF
};

/* ******* REQUESTED DIRECTION ******* */
enum Direction {
    UP,
    DOWN
};


public class Commercial_Controller {
    //------------------------------------------- GLOBAL VARIABLES ---------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------------------------------------------
    int numberOfColumns;
    let numberOfFloors;
    let numberOfElevators;
    let maxWeight;          //Maximum weight an elevator can carry in KG
    int waitingTime = 1;    // How many time the door remains opened in SECONDS


    //------------------------------------------- TESTING PROGRAM ---------------------------------------------------------------------
    //---------------------------------------------------------------------------------------------------------------------------------
    maxWeight =500; //Maximum weight an elevator can carry in KG

    /* ******* CREATE SCENARIO 1 ******* */
    function scenario1() {
        console.log();
        console.log("****************************** SCENARIO 1: ******************************");
        let columnScenario1 = new Column(1, columnStatus.ACTIVE, 10, 2); //parameters (id, columnStatus.ACTIVE/INACTIVE, numberOfFloors, numberOfElevators)
        columnScenario1.elevatorsList[0].floor = 2; //floor where the elevator 1 is
        columnScenario1.elevatorsList[1].floor = 6; //floor where the elevator 2 is

        console.log();
        console.log("Person 1: (elevator 1 is expected)"); //elevator expected
        columnScenario1.requestElevator(3, buttonDirection.UP); //parameters (requestedFloor, buttonDirection.UP/DOWN)
        columnScenario1.elevatorsList[0].requestFloor(7, columnScenario1); //parameters (requestedFloor, requestedColumn)
        console.log("==================================");
    }

    /* ******* CREATE SCENARIO 2 ******* */
    function scenario2() {
        console.log();
        console.log("****************************** SCENARIO 2: ******************************");
        let columnScenario2 = new Column(1, columnStatus.ACTIVE, 10, 2);
        columnScenario2.elevatorsList[0].floor = 10;
        columnScenario2.elevatorsList[1].floor = 3;

        console.log();
        console.log("Person 1: (elevator 2 is expected)");
        columnScenario2.requestElevator(1, buttonDirection.UP);
        columnScenario2.elevatorsList[1].requestFloor(6, columnScenario2);
        console.log("----------------------------------");
        console.log();
        console.log("Person 2: (elevator 2 is expected)");
        columnScenario2.requestElevator(3, buttonDirection.UP);
        columnScenario2.elevatorsList[1].requestFloor(5, columnScenario2);
        console.log("----------------------------------");
        console.log();
        console.log("Person 3: (elevator 1 is expected)");
        columnScenario2.requestElevator(9, buttonDirection.DOWN);
        columnScenario2.elevatorsList[0].requestFloor(2, columnScenario2);
        console.log("==================================");
    }

    /* ******* CREATE SCENARIO 3 ******* */
    function scenario3() {
        console.log();
        console.log("****************************** SCENARIO 3: ******************************");
        let columnScenario3 = new Column(1, columnStatus.ACTIVE, 10, 2);
        columnScenario3.elevatorsList[0].floor = 10;
        columnScenario3.elevatorsList[1].floor = 3;
        columnScenario3.elevatorsList[1].status = elevatorStatus.UP;

        console.log();
        console.log("Person 1: (elevator 1 is expected)");
        columnScenario3.requestElevator(3, buttonDirection.DOWN);
        columnScenario3.elevatorsList[0].requestFloor(2, columnScenario3);
        console.log("----------------------------------");
        console.log();

        //2 minutes later elevator 1(B) finished its trip to 6th floor
        columnScenario3.elevatorsList[1].floor = 6;
        columnScenario3.elevatorsList[1].status = elevatorStatus.IDLE;

        console.log("Person 2: (elevator 2 is expected)");
        columnScenario3.requestElevator(10, buttonDirection.DOWN);
        columnScenario3.elevatorsList[1].requestFloor(3, columnScenario3);
        console.log("==================================");
    }


    /* ******* CALL SCENARIOS ******* */
    scenario1();

    scenario2();

    scenario3();


    //---------------------------------------------------------------------------------------------------------------------------------
    //------------------------------------------- TEST YOUR SCENARIO ---------------------------------------------------------------------
    //---------------------------------------------------------------------------------------------------------------------------------
    //  Instruction for your test:
    //  1- Change the 'X' for a value (see the notes to fill correctly at the comments at right of each line)
    //  2- Uncomment the 'scenarioX()' at the last line of code
    //  3- Run the code using a terminal of your preference by typing: Residential_Controller.js

    function scenarioX() {
        console.log();
        console.log("****************************** SCENARIO X: ******************************");
        let columnX = new Column(X, columnStatus.X, X, X); //set parameters (id, columnStatus.ACTIVE/INACTIVE, numberOfFloors, numberOfElevators)
        columnX.elevatorsList[0].floor = X; //floor where the elevator 1 is
        columnX.elevatorsList[1].floor = X; //floor where the elevator 2 is
        // If you have more than 2 elevators, make a copy of the line above and put the corresponding index inside the brackets [X]

        console.log();
        console.log("Person X: (elevator X is expected)"); //elevator expected
        columnX.requestElevator(X, buttonDirection.X); //set parameters (requestedFloor, buttonDirection.UP/DOWN)
        columnX.elevatorsList[X].requestFloor(X, columnX); //choose elevator by index and set parameters (requestedFloor, requestedColumn)
        console.log("==================================");
    }

    // scenarioX()

}
