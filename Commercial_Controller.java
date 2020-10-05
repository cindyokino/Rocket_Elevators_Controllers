/* *********************************************** **
 @Author			Cindy Okino
 @Website		https://github.com/cindyokino
 @Last Update	October 9, 2020


 SUMMARY:
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
 7- TESTING PROGRAM - SCENARIOS
 8- TEST YOUR SCENARIO
 9- GLOBAL VARIABLES
 10- TESTING PROGRAM - CALL SCENARIOS

 CONTROLLED OBJECTS:
 Columns: controls a list of N elevators
 Elevators: controls doors, buttons, displays

  ** ************************************************** */


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

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
            sameDirectionElevatorList = activeElevatorList.stream().filter(elevator -> elevator.status.name().equals(direction.name())).collect(Collectors.toList());
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
    public void requestElevator(int requestedFloor, Direction direction, int waitingTime) {
        if (direction == Direction.UP) {
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
        bestElevator.moveElevator(requestedFloor, this, waitingTime);
    }
}


//------------------------------------------- ELEVATOR CLASS -----------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------------------------
class Elevator {
    int id;
    int numberOfFloors;
    int floor;
    ElevatorStatus status;
    SensorStatus weightSensorStatus;
    SensorStatus obstructionSensorStatus;
    Door elevatorDoor;
    Display elevatorDisplay;
    List<Door> floorDoorsList;
    List<Display> floorDisplaysList;
    List<Button> floorButtonsList;
    List<Integer> floorList;

    //----------------- Constructor and its attributes -----------------//
    public Elevator(int id, int numberOfFloors, int floor, ElevatorStatus elevatorStatus, SensorStatus weightSensorStatus, SensorStatus obstructionSensorStatus) {
        this.id = id;
        this.numberOfFloors = numberOfFloors;
        this.floor = floor;
        this.status = elevatorStatus;
        this.weightSensorStatus = weightSensorStatus;
        this.obstructionSensorStatus = obstructionSensorStatus;
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
    public void moveElevator(int requestedFloor, Column requestedColumn, int waitingTime) {
        while (this.floorList.size() != 0) {
            if (this.status == ElevatorStatus.IDLE) {
                if (this.floor < requestedFloor) {
                    this.status = ElevatorStatus.UP;
                } else if (this.floor == requestedFloor) {
                    this.openDoors(waitingTime);
                    this.deleteFloorFromList(requestedFloor);
                    requestedColumn.buttonsUpList.get(requestedFloor - 1).status = ButtonStatus.OFF;
                    requestedColumn.buttonsDownList.get(requestedFloor - 1).status = ButtonStatus.OFF;
                    this.floorButtonsList.get(requestedFloor - 1).status = ButtonStatus.OFF;
                } else {
                    this.status = ElevatorStatus.DOWN;
                }
            }
            if (this.status == ElevatorStatus.UP) {
                this.moveUp(requestedColumn, waitingTime);
            } else {
                this.moveDown(requestedColumn, waitingTime);
            }
        }
    }

    /* ******* LOGIC TO MOVE UP ******* */
    public void moveUp(Column requestedColumn, int waitingTime) {
        List<Integer> tempArray = new ArrayList<>(this.floorList);
        for (int i = this.floor; i < tempArray.get(tempArray.size() - 1); i++) {
            if (this.floorDoorsList.get(i).status == DoorStatus.OPENED || this.elevatorDoor.status == DoorStatus.OPENED) {
                System.out.println("   Doors are open, closing doors before move up");
                this.closeDoors();
            }
            System.out.println("Moving elevator" + this.id + " <up> from floor " + i + " to floor " + (i + 1));
            int nextFloor = (i + 1);
            this.floor = nextFloor;
            this.updateDisplays(this.floor);

            if (tempArray.contains(nextFloor)) {
                this.openDoors(waitingTime);
                this.deleteFloorFromList(nextFloor);
                requestedColumn.buttonsUpList.get(i - 1).status = ButtonStatus.OFF;
                this.floorButtonsList.get(i).status = ButtonStatus.OFF;
            }
        }
        if (this.floorList.size() == 0) {
            this.status = ElevatorStatus.IDLE;
//            System.out.println("       Elevator"+ this.id + " is now " + this.status);
        } else {
            this.status = ElevatorStatus.DOWN;
            System.out.println("       Elevator" + this.id + " is now going " + this.status);
        }
    }

    /* ******* LOGIC TO MOVE DOWN ******* */
    public void moveDown(Column requestedColumn, int waitingTime) {
        List<Integer> tempArray = new ArrayList<>(this.floorList);
        for (int i = this.floor; i > tempArray.get(tempArray.size() - 1); i--) {
            if (this.floorDoorsList.get(i - 1).status == DoorStatus.OPENED || this.elevatorDoor.status == DoorStatus.OPENED) {
                System.out.println("       Doors are open, closing doors before move down");
                this.closeDoors();
            }
            System.out.println("Moving elevator" + this.id + " <down> from floor " + i + " to floor " + (i - 1));
            int nextFloor = (i - 1);
            this.floor = nextFloor;
            this.updateDisplays(this.floor);

            if (tempArray.contains(nextFloor)) {
                this.openDoors(waitingTime);
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
        System.out.println("       Elevator" + this.id + " doors are opened");
        this.elevatorDoor.status = DoorStatus.OPENED;
        this.floorDoorsList.get(this.floor - 1).status = DoorStatus.OPENED;
        try {
            Thread.sleep(waitingTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.closeDoors();
    }

    /* ******* LOGIC TO CLOSE DOORS ******* */
    public void closeDoors() {
        if (this.weightSensorStatus == SensorStatus.OFF && this.obstructionSensorStatus  == SensorStatus.OFF) {
            System.out.println("       Closing doors...");
            System.out.println("       Elevator" + this.id + " doors are closed");
            this.floorDoorsList.get(this.floor - 1).status = DoorStatus.CLOSED;
            this.elevatorDoor.status = DoorStatus.CLOSED;
        }
    }

    /* ******* LOGIC FOR WEIGHT SENSOR ******* */
    public void checkWeight(int maxWeight) {
        Random random = new Random();
        int randomWeight = random.nextInt(maxWeight + 100); //This random simulates the weight from a weight sensor
        while (randomWeight > maxWeight) {
            this.weightSensorStatus = SensorStatus.ON;
            System.out.println("       ! Elevator capacity reached, waiting until the weight is lower before continue...");
            randomWeight -= 100; //I'm supposing the random number is 600, I'll subtract 101 so it will be less than 500 (the max weight I proposed) for the second time it runs
        }
        this.weightSensorStatus= SensorStatus.OFF;
        System.out.println("       Elevator capacity is OK");
    }

    /* ******* LOGIC FOR OBSTRUCTION SENSOR ******* */
    public void checkObstruction() {
        int probabilityNotBlocked = 70;
        Random random = new Random();
        int number = random.nextInt(100); //This random simulates the probability of an obstruction (I supposed 30% of chance something is blocking the door)
        while (number > probabilityNotBlocked) {
            this.obstructionSensorStatus= SensorStatus.ON;
            System.out.println("       ! Elevator door is blocked by something, waiting until door is free before continue...");
            number -= 30; //I'm supposing the random number is 100, I'll subtract 30 so it will be less than 70 (30% probability), so the second time it runs theres no one blocking the door
        }
        this.obstructionSensorStatus= SensorStatus.OFF;
        System.out.println("       Elevator door is FREE");
    }

    /* ******* LOGIC TO ADD A FLOOR TO THE FLOOR LIST ******* */
    public void addFloorToFloorList(int floor) {
        this.floorList.add(floor);
        Collections.sort(this.floorList);
    }

    /* ******* LOGIC TO DELETE ITEM FROM FLOORS LIST ******* */
    public void deleteFloorFromList(int stopFloor) {
        int index = this.floorList.indexOf(stopFloor);
        if (index > -1) {
            this.floorList.remove(index);
        }
    }


    //----------------- Entry method -----------------//
    /* ******* ENTRY METHOD ******* */
    /* ******* REQUEST FOR A FLOOR BY PRESSING THE FLOOR BUTTON INSIDE THE ELEVATOR ******* */
    public void requestFloor(int requestedFloor, Column requestedColumn, int maxWeight, int waitingTime) {
        System.out.println();
        System.out.println(" >> Someone inside the elevator" + this.id + " wants to go to floor <" + requestedFloor + "> <<");
        this.checkWeight(maxWeight);
        this.checkObstruction();
        this.addFloorToFloorList(requestedFloor);
        this.moveElevator(requestedFloor, requestedColumn, waitingTime);
    }
}


//------------------------------------------- DOOR CLASS -----------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------------------------
class Door {
    int id;
    DoorStatus status;
    int floor;

    public Door(int id, DoorStatus doorStatus, int floor) {
        this.id = id;
        this.status = doorStatus;
        this.floor = floor;
    }
}


//------------------------------------------- BUTTON CLASS -----------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------------------------
class Button {
    int id;
    ButtonStatus status;
    int floor;

    public Button(int id, ButtonStatus buttonStatus, int floor) {
        this.id = id;
        this.status = buttonStatus;
        this.floor = floor;
    }
}


//------------------------------------------- DISPLAY CLASS -----------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------------------------
class Display {
    int id;
    DisplayStatus status;
    int floor;

    public Display(int id, DisplayStatus displayStatus, int floor) {
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
}

/* ******* ELEVATOR STATUS ******* */
enum ElevatorStatus {
    IDLE,
    UP,
    DOWN
}

/* ******* BUTTONS STATUS ******* */
enum ButtonStatus {
    ON,
    OFF
}

/* ******* SENSORS STATUS ******* */
enum SensorStatus {
    ON,
    OFF
}

/* ******* DOORS STATUS ******* */
enum DoorStatus {
    OPENED,
    CLOSED
}

/* ******* DISPLAY STATUS ******* */
enum DisplayStatus {
    ON,
    OFF
}

/* ******* REQUESTED DIRECTION ******* */
enum Direction {
    UP,
    DOWN
}


public class Commercial_Controller {
    //------------------------------------------- TESTING PROGRAM - SCENARIOS ----------------------------------------------------------
    //---------------------------------------------------------------------------------------------------------------------------------

    /* ******* CREATE SCENARIO 1 ******* */
    public static void scenario1 (int waitingTime, int maxWeight) {
        System.out.println();
        System.out.println("****************************** SCENARIO 1: ******************************");
        Column columnScenario1 = new Column(1, ColumnStatus.ACTIVE, 10, 2); //parameters (id, ColumnStatus.ACTIVE/INACTIVE, numberOfFloors, numberOfElevators)
        columnScenario1.elevatorsList.get(0).floor = 2; //floor where the elevator 1 is
        columnScenario1.elevatorsList.get(1).floor = 6; //floor where the elevator 2 is

        System.out.println();
        System.out.println("Person 1: (elevator 1 is expected)"); //elevator expected
        columnScenario1.requestElevator(3, Direction.UP, waitingTime); //parameters (requestedFloor, buttonDirection.UP/DOWN)
        columnScenario1.elevatorsList.get(0).requestFloor(7, columnScenario1, maxWeight,waitingTime); //parameters (requestedFloor, requestedColumn, maxWeight)
        System.out.println("==================================");
    }

    /* ******* CREATE SCENARIO 2 ******* */
    public static void scenario2 (int waitingTime, int maxWeight) {
        System.out.println();
        System.out.println("****************************** SCENARIO 2: ******************************");
        Column columnScenario2 = new Column(1, ColumnStatus.ACTIVE, 10, 2);
        columnScenario2.elevatorsList.get(0).floor = 10;
        columnScenario2.elevatorsList.get(1).floor = 3;

        System.out.println();
        System.out.println("Person 1: (elevator 2 is expected)");
        columnScenario2.requestElevator(1, Direction.UP, waitingTime);
        columnScenario2.elevatorsList.get(1).requestFloor(6, columnScenario2, maxWeight, waitingTime);
        System.out.println("----------------------------------");
        System.out.println();
        System.out.println("Person 2: (elevator 2 is expected)");
        columnScenario2.requestElevator(3, Direction.UP, waitingTime);
        columnScenario2.elevatorsList.get(1).requestFloor(5, columnScenario2, maxWeight, waitingTime);
        System.out.println("----------------------------------");
        System.out.println();
        System.out.println("Person 3: (elevator 1 is expected)");
        columnScenario2.requestElevator(9, Direction.DOWN, waitingTime);
        columnScenario2.elevatorsList.get(0).requestFloor(2, columnScenario2, maxWeight,waitingTime);
        System.out.println("==================================");
    }

    /* ******* CREATE SCENARIO 3 ******* */
    public static void scenario3 (int waitingTime, int maxWeight) {
        System.out.println();
        System.out.println("****************************** SCENARIO 3: ******************************");
        Column columnScenario3 = new Column(1, ColumnStatus.ACTIVE, 10, 2);
        columnScenario3.elevatorsList.get(0).floor = 10;
        columnScenario3.elevatorsList.get(1).floor = 3;
        columnScenario3.elevatorsList.get(1).status = ElevatorStatus.UP;

        System.out.println();
        System.out.println("Person 1: (elevator 1 is expected)");
        columnScenario3.requestElevator(3, Direction.DOWN,waitingTime);
        columnScenario3.elevatorsList.get(0).requestFloor(2, columnScenario3, maxWeight,waitingTime);
        System.out.println("----------------------------------");
        System.out.println();

        //2 minutes later elevator 1(B) finished its trip to 6th floor
        columnScenario3.elevatorsList.get(1).floor = 6;
        columnScenario3.elevatorsList.get(1).status = ElevatorStatus.IDLE;

        System.out.println("Person 2: (elevator 2 is expected)");
        columnScenario3.requestElevator(10, Direction.DOWN, waitingTime);
        columnScenario3.elevatorsList.get(1).requestFloor(3, columnScenario3, maxWeight, waitingTime);
        System.out.println("==================================");
    }


    //------------------------------------------- TEST YOUR SCENARIO ---------------------------------------------------------------------
    //---------------------------------------------------------------------------------------------------------------------------------
    //  Instruction for your test:
    // 1- Uncomment the scenarioX() function
    // 2- Change the 'X' for a value (see the notes to fill correctly at the comments at right of each line)
    // 3- Uncomment the 'scenarioX()' at the end of the file
    // 4- Run the code using a terminal of your preference. Before you need to compile the file by typing: javac Residential_Controller.java
    //    Then it will generate a new file and you can run called Residential_Controller.class and now you can run the program by typing: java Residential_Controller
    //    Or you can just run the program using an IDE (integrated development environment) like IntelliJ IDEA, Eclipse, NetBeans, etc

//    public static void scenarioX (int waitingTime, int maxWeight) {
//        System.out.println();
//        System.out.println("****************************** SCENARIO X: ******************************");
//        Column columnX = new Column(X, ColumnStatus.X, X, X); //set parameters (id, ColumnStatus.ACTIVE/INACTIVE, numberOfFloors, numberOfElevators)
//        columnX.elevatorsList.get(0).floor = X; //floor where the elevator 1 is
//        columnX.elevatorsList.get(1).floor = X; //floor where the elevator 2 is
//        // If you have more than 2 elevators, make a copy of the line above and put the corresponding index inside the parenthesis .get(X)
//
//        System.out.println();
//        System.out.println("Person X: (elevator X is expected)"); //elevator expected
//        columnX.requestElevator(X, Direction.X, waitingTime); //set parameters (requestedFloor, Direction.UP/DOWN)
//        columnX.elevatorsList.get(X).requestFloor(X, columnX, maxWeight, waitingTime); //choose elevator by index and set parameters (requestedFloor, requestedColumn, maxWeight)
//        System.out.println("==================================");
//    }


    public static void main(String[] args) {
        //------------------------------------------- GLOBAL VARIABLES ---------------------------------------------------------------------
        //----------------------------------------------------------------------------------------------------------------------------------
        int maxWeight = 500;     //Maximum weight an elevator can carry in KG
        int waitingTime = 1;     // How many time the door remains opened in SECONDS


        //------------------------------------------- TESTING PROGRAM - CALL SCENARIOS ---------------------------------------------------------------------
        //----------------------------------------------------------------------------------------------------------------------------------
        /* ******* CALL SCENARIOS ******* */
        scenario1(waitingTime, maxWeight);
        scenario2(waitingTime, maxWeight);
        scenario3(waitingTime, maxWeight);

        /* ******* CALL YOUR SCENARIO ******* */
        // scenarioX(waitingTime, maxWeight)
    }
}
