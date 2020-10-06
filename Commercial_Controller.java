/* *********************************************** **
 @Author		Cindy Okino
 @Website		https://github.com/cindyokino
 @Last Update	October 9, 2020


 SUMMARY:
 0- BATTERY CLASS
    0a- Constructor and its attributes
    0b- Method toString
    0c- Methods to create a list: createColumnsList
    0d- Methods for logic: calculateNumberOfFloorsPerColumn
 1- COLUMN CLASS
    1a- Constructor and its attributes
    1b- Method toString
    1c- Methods to create a list: createElevatorsList, createButtonsUpList, createButtonsDownList
    1d- Methods for logic: findElevator, findNearestElevator
    1e- Entry method: requestElevator
 2- ELEVATOR CLASS
    2a- Constructor and its attributes
    2b- Method toString
    2c- Methods to create a list: createFloorDoorsList, createDisplaysList, createFloorButtonsList, addFloorToFloorList
    2d- Methods for logic: moveElevator, moveUp, moveDown, updateDisplays, openDoors, closeDoors, checkWeight, checkObstruction, deleteFloorFromList
    2e- Entry method: requestFloor
 3- DOOR CLASS
 4- BUTTON CLASS
 5- DISPLAY CLASS
 6- ENUMS
 7- TESTING PROGRAM - SCENARIOS
 8- TEST YOUR SCENARIO
 9- TESTING PROGRAM - CALL SCENARIOS

 CONTROLLED OBJECTS:
 Battery: contains a list of N columns
 Columns: controls a list of N elevators
 Elevators: controls doors, buttons, displays

 numberOfBasements                                                  //Use a negative number
 numberOfFloors                                                     //Floors of the building excluding the number of basements
 totalNumberOfFloors = numberOfFloors + Math.abs(numberOfBasements) //Transform the number of basements to a positive number
 minBuildingFloor                                                   //Is equal to 1 OR equal the numberOfBasements if there is a basement
 maxBuildingFloor = numberOfFloors                                  //Is the last floor of the building
 maxWeight                                                          //Maximum weight an elevator can carry in KG

  ** ************************************************** */


import javax.swing.text.html.Option;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;


//------------------------------------------- BATTERY CLASS -----------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------------------------
class Battery {
    int id;
    int numberOfColumns;
    int minBuildingFloor;                  //Is equal to 1 OR equal the numberOfBasements if there is a basement
    int maxBuildingFloor;                  //Is the last floor of the building
    int numberOfFloors;                    //Floors of the building excluding the number of basements
    int numberOfBasements;
    int totalNumberOfFloors;               //numberOfFloors + numberOfBasements
    int numberOfElevatorsPerColumn;
    int numberOfFloorsPerColumn;
    BatteryStatus status;
    List<Column> columnsList;

    //----------------- Constructor and its attributes -----------------//
    public Battery(int id, int numberOfColumns, int totalNumberOfFloors, int numberOfBasements, int numberOfElevatorsPerColumn, BatteryStatus batteryStatus) {
        this.id = id;
        this.numberOfColumns = numberOfColumns;
        this.totalNumberOfFloors = totalNumberOfFloors;
        this.numberOfBasements = numberOfBasements;
        this.numberOfElevatorsPerColumn = numberOfElevatorsPerColumn;
        this.status = batteryStatus;
        this.columnsList = new ArrayList<>();
        this.numberOfFloorsPerColumn = calculateNumberOfFloorsPerColumn();
        this.createColumnsList();
        this.setColumnValues();
        this.createListsInsideColumns();
    }


    //----------------- Method toString -----------------//
    /* ******* GET A STRING REPRESENTATION OF COLUMN OBJECT ******* */
    @Override
    public String toString() {
        return "battery" + this.id + " | Basements: " + this.numberOfBasements + " | Columns: " + this.numberOfColumns + " | Elevators per column: " + this.numberOfElevatorsPerColumn;
    }


    //----------------- Methods to create a list -----------------//
    /* ******* CREATE A LIST OF COLUMNS FOR THE BATTERY ******* */
    public void createColumnsList() {
        char name = 'A';
        for (int i = 1; i <= this.numberOfColumns; i++) {
            this.columnsList.add(new Column(i, name, ColumnStatus.ACTIVE, this.numberOfElevatorsPerColumn, numberOfFloorsPerColumn, numberOfBasements, this)); //****************************************************************************************
            name += 1;
        }
    }

    /* ******* CREATE A LIST OF COLUMNS FOR THE BATTERY ******* */
    public void createListsInsideColumns() {
        columnsList.forEach(column -> {
            column.createElevatorsList();
            column.createButtonsUpList();
            column.createButtonsDownList(numberOfBasements);
        });
    }


    //----------------- Methods for logic -----------------//
    /* ******* LOGIC TO FIND THE FLOORS SERVED PER EACH COLUMN ******* */
    public int calculateNumberOfFloorsPerColumn() {
        numberOfFloors = totalNumberOfFloors - numberOfBasements;
        int numberOfFloorsPerColumn;

        if (this.numberOfBasements > 0) { //if there is basement floors
            numberOfFloorsPerColumn = (this.numberOfFloors / (this.numberOfColumns - 1)); //the first column serves the basement floors
        } else { //there is no basement
            numberOfFloorsPerColumn = (this.numberOfFloors / this.numberOfColumns);
        }

        return numberOfFloorsPerColumn;
    }


    /* ******* LOGIC TO FIND THE REMAINING FLOORS OF EACH COLUMN AND SET VALUES servedFloors, minFloors, maxFloors ******* */
    public void setColumnValues() {
        int remainingFloors;

        //calculating the remaining floors
        if (this.numberOfBasements > 0) { //if there are basement floors
            remainingFloors = this.numberOfFloors % (this.numberOfColumns - 1);
        } else { //there is no basement
            remainingFloors = this.numberOfFloors % this.numberOfColumns;
        }

        //setting the minFloor and maxFloor of each column
        int minimumFloor = 1;
        if (this.numberOfColumns == 1) { //if there is just one column, it serves all the floors of the building
            this.columnsList.get(0).numberServedFloors = totalNumberOfFloors;
            if (numberOfBasements > 0) { //if there is basement
                this.columnsList.get(0).minFloor = (numberOfBasements * -1);
            } else { //if there is NO basement
                this.columnsList.get(0).minFloor = minimumFloor;
                this.columnsList.get(0).maxFloor = numberOfFloors;
            }
        } else { //for more than 1 column
            for (int i = 1; i < this.columnsList.size(); i++) { //if its not the first column (because the first column serves the basements)
                if (i == 1) {
                    this.columnsList.get(i).numberServedFloors = numberOfFloorsPerColumn;
                } else {
                    this.columnsList.get(i).numberServedFloors = (numberOfFloorsPerColumn + 1); //Add 1 floor for the RDC/ground floor
                }
                this.columnsList.get(i).minFloor = minimumFloor;
                this.columnsList.get(i).maxFloor = (this.columnsList.get(i).minFloor + numberOfFloorsPerColumn - 1);
                minimumFloor = this.columnsList.get(i).maxFloor + 1; //setting the minimum floor for the next column
            }

            //adjusting the number of served floors of the columns if there are remaining floors
            if (remainingFloors != 0) { //if the remainingFloors is not zero, then it adds the remaining floors to the last column
                this.columnsList.get(this.columnsList.size() - 1).numberServedFloors = numberOfFloorsPerColumn + remainingFloors;
                this.columnsList.get(this.columnsList.size() - 1).maxFloor = this.columnsList.get(this.columnsList.size() - 1).minFloor + this.columnsList.get(this.columnsList.size() - 1).numberServedFloors;
            }
            //if there is a basement, then the first column will serve the basements + RDC
            if (this.numberOfBasements > 0) {
                this.columnsList.get(0).numberServedFloors = (this.numberOfBasements + 1); //+1 is the RDC
                this.columnsList.get(0).minFloor = (numberOfBasements * -1); //the minFloor of basement is a negative number
                this.columnsList.get(0).maxFloor = 1; //1 is the RDC
            }
        }
    }

}


//------------------------------------------- COLUMN CLASS ------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------------------------
class Column {
    int id;
    char name;
    ColumnStatus status;
    int numberOfElevatorsPerColumn;
    int minFloor;
    int maxFloor;
    int numberServedFloors;
    int numberOfBasements;
    Battery battery;
    List<Elevator> elevatorsList;
    List<Button> buttonsUpList;
    List<Button> buttonsDownList;

    //----------------- Constructor and its attributes -----------------//
    public Column(int id, char name, ColumnStatus columnStatus, int numberOfElevatorsPerColumn, int numberServedFloors, int numberOfBasements, Battery battery) {
        this.id = id;
        this.name = name;
        this.status = columnStatus;
        this.numberOfElevatorsPerColumn = numberOfElevatorsPerColumn;
        this.numberServedFloors = numberServedFloors;
        this.numberOfBasements = numberOfBasements;
        this.battery = battery;
        this.elevatorsList = new ArrayList<>();
        this.buttonsUpList = new ArrayList<>();
        this.buttonsDownList = new ArrayList<>();
    }


    //----------------- Method toString -----------------//
    /* ******* GET A STRING REPRESENTATION OF BATTERY OBJECT ******* */
    @Override
    public String toString() {
        return "column" + this.name + " | Served floors: " + this.numberServedFloors + " | Min floor: " + this.minFloor + " | Max floor: " + this.maxFloor;
    }


    //----------------- Methods to create a list -----------------//
    /* ******* CREATE A LIST OF ELEVATORS FOR THE COLUMN ******* */
    public void createElevatorsList() {
        for (int i = 1; i <= this.numberOfElevatorsPerColumn; i++) {
            this.elevatorsList.add(new Elevator(i, this.numberServedFloors, 1, ElevatorStatus.IDLE, SensorStatus.OFF, SensorStatus.OFF, this));
        }
    }

    /* ******* CREATE A LIST WITH UP BUTTONS FROM THE FIRST FLOOR TO THE LAST LAST BUT ONE FLOOR ******* */
    public void createButtonsUpList() {
        buttonsUpList.add(new Button(1, ButtonStatus.OFF, 1));
        for (int i = minFloor; i < this.maxFloor; i++) {
            this.buttonsUpList.add(new Button(i, ButtonStatus.OFF, i));
        }
    }

    /* ******* CREATE A LIST WITH DOWN BUTTONS FROM THE SECOND FLOOR TO THE LAST FLOOR ******* */
    public void createButtonsDownList(int numberOfBasements) {
        buttonsDownList.add(new Button(1, ButtonStatus.OFF, 1));
        int minBuildingFloor;
        if (numberOfBasements > 0) {
            minBuildingFloor = numberOfBasements;
        } else {
            minBuildingFloor = 1;
        }
        for (int i = (minBuildingFloor + 1); i <= this.maxFloor; i++) {
            this.buttonsDownList.add(new Button(i, ButtonStatus.OFF, i));
        }
    }


    //----------------- Methods for logic -----------------//
    /* ******* LOGIC TO OPTIMIZE THE ELEVATORS DISPLACEMENTS ******* */
    public void optimizeDisplacement(List<Elevator> elevatorsList) {
        LocalTime morningPeakStart = LocalTime.of(6, 0);
        LocalTime morningPeakEnd = LocalTime.of(10, 0);
        LocalTime eveningPeakStart = LocalTime.of(16, 0);
        LocalTime eveningPeakEnd = LocalTime.of(19, 0);
        elevatorsList.forEach(elevator -> {
            if (LocalTime.now().isAfter(morningPeakStart) && LocalTime.now().isBefore(morningPeakEnd)) {
                elevator.moveElevator(1);
            } else if (LocalTime.now().isAfter(eveningPeakStart) && LocalTime.now().isBefore(eveningPeakEnd)) {
                elevator.moveElevator(this.maxFloor);
            }
        });
    }

    /* ******* LOGIC TO FIND THE BEST ELEVATOR WITH A PRIORITIZATION LOGIC ******* */
    public Elevator findElevator(int currentFloor, Direction direction) {
        Elevator bestElevator;
        List<Elevator> activeElevatorList = new ArrayList<>();
        List<Elevator> idleElevatorList = new ArrayList<>();
        List<Elevator> sameDirectionElevatorList = new ArrayList<>();
        this.elevatorsList.forEach(elevator -> {
            if (elevator.status != ElevatorStatus.IDLE) {
                //Verify if the request is on the elevators way, otherwise the elevator will just continue its way ignoring this call
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
        System.out.println("-----------------------------------------------------");
        System.out.println("   > > >> >>> ELEVATOR " + this.name + bestElevator.id + " WAS CALLED <<< << < <");
        System.out.println("-----------------------------------------------------");

        return bestElevator;
    }


    //----------------- Entry method -----------------//
    /* ******* ENTRY METHOD ******* */
    /* ******* REQUEST FOR AN ELEVATOR BY PRESSING THE UP OU DOWN BUTTON OUTSIDE THE ELEVATOR ******* */
    public void requestElevator(int requestedFloor, Direction direction) { // User goes to the specific column and press a button outside the elevator requesting for an elevator
        if (direction == Direction.UP) {
            this.buttonsUpList.get(requestedFloor - 1).status = ButtonStatus.ON;
        } else {
            this.buttonsDownList.get(requestedFloor - 2).status = ButtonStatus.ON;
        }
        System.out.println(">> Someone request an elevator from floor <" + requestedFloor + "> and direction <" + direction + "> <<");
        Elevator bestElevator = this.findElevator(requestedFloor, direction);
        bestElevator.moveElevator(requestedFloor);
    }
}


//------------------------------------------- ELEVATOR CLASS ----------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------------------------
class Elevator {
    int id;
    int numberServedFloors;
    int floor;
    ElevatorStatus status;
    SensorStatus weightSensorStatus;
    SensorStatus obstructionSensorStatus;
    Column column;
    Door elevatorDoor;
    Display elevatorDisplay;
    List<Door> floorDoorsList;
    List<Display> floorDisplaysList;
    List<Button> floorButtonsList;
    List<Integer> floorList;

    //----------------- Constructor and its attributes -----------------//
    public Elevator(int id, int numberServedFloors, int floor, ElevatorStatus elevatorStatus, SensorStatus weightSensorStatus, SensorStatus obstructionSensorStatus, Column column) {
        this.id = id;
        this.numberServedFloors = numberServedFloors;
        this.floor = floor;
        this.status = elevatorStatus;
        this.weightSensorStatus = weightSensorStatus;
        this.obstructionSensorStatus = obstructionSensorStatus;
        this.column = column;
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


    //----------------- Method toString -----------------//
    /* ******* GET A STRING REPRESENTATION OF ELEVATOR OBJECT ******* */
    @Override
    public String toString() {
        return "elevator" + column.name + this.id + " | Floors: " + this.floor + " | Status: " + this.status;
    }


    //----------------- Methods to create a list -----------------//
    /* ******* CREATE A LIST WITH A DOOR OF EACH FLOOR ******* */
    public void createFloorDoorsList() {
        floorDoorsList.add(new Door(1, DoorStatus.CLOSED, 1));
        for (int i = column.minFloor; i <= this.column.maxFloor; i++) {
            this.floorDoorsList.add(new Door(i, DoorStatus.CLOSED, i));
        }
    }

    /* ******* CREATE A LIST WITH A DISPLAY OF EACH FLOOR ******* */
    public void createDisplaysList() {
        floorDisplaysList.add(new Display(1, DisplayStatus.ON, 1));
        for (int i = column.minFloor; i <= this.column.maxFloor; i++) {
            this.floorDisplaysList.add(new Display(i, DisplayStatus.ON, i));
        }
    }

    /* ******* CREATE A LIST WITH A BUTTON OF EACH FLOOR ******* */
    public void createFloorButtonsList() {
        floorButtonsList.add(new Button(1, ButtonStatus.OFF, 1));
        for (int i = column.minFloor; i <= this.column.maxFloor; i++) {
            this.floorButtonsList.add(new Button(i, ButtonStatus.OFF, i));
        }
    }


    //----------------- Methods for logic -----------------//
    /* ******* LOGIC TO MOVE ELEVATOR ******* */
    public void moveElevator(int requestedFloor) {
        this.addFloorToFloorList(requestedFloor);

        while (this.floorList.size() > 0) {
            if (this.status == ElevatorStatus.IDLE) {
                if (this.floor < requestedFloor) {
                    this.status = ElevatorStatus.UP;
                } else if (this.floor > requestedFloor) {
                    this.status = ElevatorStatus.DOWN;
                } else {
                    this.openDoors();
                    this.deleteFloorFromList(requestedFloor);
                    this.column.buttonsUpList.get(requestedFloor - 1).status = ButtonStatus.OFF;
                    this.column.buttonsDownList.get(requestedFloor - 1).status = ButtonStatus.OFF;
                    this.floorButtonsList.get(requestedFloor - 1).status = ButtonStatus.OFF;
                }
            }
            if (this.status == ElevatorStatus.UP) {
                this.moveUp();
            } else if (this.status == ElevatorStatus.DOWN) {
                this.moveDown();
            }
        }
    }

    /* ******* LOGIC TO MOVE UP ******* */
    public void moveUp() {
        List<Integer> tempArray = new ArrayList<>(this.floorList);
        for (int i = this.floor; i < tempArray.get(tempArray.size() - 1); i++) {
            final int j = i;
            Optional<Door> currentDoor = this.floorDoorsList.stream().filter(door -> door.id == j).findFirst();
            if (currentDoor.isPresent() && currentDoor.get().status == DoorStatus.OPENED || this.elevatorDoor.status == DoorStatus.OPENED) {
                System.out.println("   Doors are open, closing doors before move up");
                this.closeDoors();
            }
            System.out.println("Moving elevator" + column.name + this.id + " <up> from floor " + i + " to floor " + (i + 1));
            int nextFloor = (i + 1);
            this.floor = nextFloor;
            this.updateDisplays(this.floor);

            if (tempArray.contains(nextFloor)) {
                this.openDoors();
                this.deleteFloorFromList(nextFloor);
//                this.column.buttonsUpList.get(i - 1).status = ButtonStatus.OFF;
                Optional<Button> currentUpButton = this.column.buttonsUpList.stream().filter(button -> button.id == nextFloor).findFirst();
                if (currentUpButton.isPresent()) {
                    currentUpButton.get().status = ButtonStatus.OFF;
                }
//                this.floorButtonsList.get(i).status = ButtonStatus.OFF;
                Optional<Button> currentFloorButton = this.floorButtonsList.stream().filter(button -> button.id == nextFloor).findFirst();
                if (currentFloorButton.isPresent()) {
                    currentFloorButton.get().status = ButtonStatus.OFF;
                }
            }
        }
        if (this.floorList.size() == 0) {
//            column.optimizeDisplacement(this.column.elevatorsList);
            this.status = ElevatorStatus.IDLE;
//            System.out.println("       Elevator"+ this.id + " is now " + this.status);
        } else {
            this.status = ElevatorStatus.DOWN;
            System.out.println("       Elevator" + this.id + " is now going " + this.status);
        }
    }

    /* ******* LOGIC TO MOVE DOWN ******* */
    public void moveDown() {
        List<Integer> tempArray = new ArrayList<>(this.floorList);
        for (int i = this.floor; i > tempArray.get(tempArray.size() - 1); i--) {
//            if (this.floorDoorsList.get(i - 1).status == DoorStatus.OPENED || this.elevatorDoor.status == DoorStatus.OPENED) {
            final int j = i;
            Optional<Door> currentDoor = this.floorDoorsList.stream().filter(door -> door.id == j).findFirst();
            if (currentDoor.isPresent() && currentDoor.get().status == DoorStatus.OPENED || this.elevatorDoor.status == DoorStatus.OPENED) {
                System.out.println("       Doors are open, closing doors before move down");
                this.closeDoors();
            }
            System.out.println("Moving elevator" + column.name + this.id + " <down> from floor " + i + " to floor " + (i - 1));
            int nextFloor = (i - 1);
            this.floor = nextFloor;
            this.updateDisplays(this.floor);

            if (tempArray.contains(nextFloor)) {
                this.openDoors();
                this.deleteFloorFromList(nextFloor);
                this.column.buttonsDownList.get(i - 2).status = ButtonStatus.OFF;
//                this.floorButtonsList.get(i - 1).status = ButtonStatus.OFF;
                Optional<Button> currentFloorButton = this.floorButtonsList.stream().filter(button -> button.id == nextFloor).findFirst();
                if (currentFloorButton.isPresent()) {
                    currentFloorButton.get().status = ButtonStatus.OFF;
                }
            }
        }
        if (this.floorList.size() == 0) {
//            column.optimizeDisplacement(this.column.elevatorsList);
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
//        System.out.println("Displays show #" + elevatorFloor);
    }

    /* ******* LOGIC TO OPEN DOORS ******* */
    public void openDoors() {
        System.out.println("       Opening doors...");
        System.out.println("       Elevator" + this.id + " doors are opened");
        this.elevatorDoor.status = DoorStatus.OPENED;
//        this.floorDoorsList.get(this.floor - 1).status = DoorStatus.OPENED;
        Optional<Door> currentDoor = this.floorDoorsList.stream().filter(door -> door.id == this.floor).findFirst();
        if (currentDoor.isPresent()) {
            currentDoor.get().status = DoorStatus.OPENED;
        }

        try {
            Thread.sleep(1000); //How many time the door remains opened in MILLISECONDS
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.closeDoors();
    }

    /* ******* LOGIC TO CLOSE DOORS ******* */
    public void closeDoors() {
        if (this.weightSensorStatus == SensorStatus.OFF && this.obstructionSensorStatus == SensorStatus.OFF) { //Security logic
            System.out.println("       Closing doors...");
            System.out.println("       Elevator" + this.id + " doors are closed");
//            this.floorDoorsList.get(this.floor - 1).status = DoorStatus.CLOSED;
            Optional<Door> currentDoor = this.floorDoorsList.stream().filter(door -> door.id == this.floor).findFirst();
            if (currentDoor.isPresent()) {
                currentDoor.get().status = DoorStatus.CLOSED;
            }
            this.elevatorDoor.status = DoorStatus.CLOSED;
        }
    }

    /* ******* LOGIC FOR WEIGHT SENSOR ******* */
    public void checkWeight() {
        int maxWeight = 500; //Maximum weight an elevator can carry in KG
        Random random = new Random();
        int randomWeight = random.nextInt(maxWeight + 100); //This random simulates the weight from a weight sensor
        while (randomWeight > maxWeight) {  //Logic of loading
            this.weightSensorStatus = SensorStatus.ON;  //Detect a full elevator
            System.out.println("       ! Elevator capacity reached, waiting until the weight is lower before continue...");
            randomWeight -= 100; //I'm supposing the random number is 600, I'll subtract 101 so it will be less than 500 (the max weight I proposed) for the second time it runs
        }
        this.weightSensorStatus = SensorStatus.OFF;
        System.out.println("       Elevator capacity is OK");
    }

    /* ******* LOGIC FOR OBSTRUCTION SENSOR ******* */
    public void checkObstruction() {
        int probabilityNotBlocked = 70;
        Random random = new Random();
        int number = random.nextInt(100); //This random simulates the probability of an obstruction (I supposed 30% of chance something is blocking the door)
        while (number > probabilityNotBlocked) {
            this.obstructionSensorStatus = SensorStatus.ON;
            System.out.println("       ! Elevator door is blocked by something, waiting until door is free before continue...");
            number -= 30; //I'm supposing the random number is 100, I'll subtract 30 so it will be less than 70 (30% probability), so the second time it runs theres no one blocking the door
        }
        this.obstructionSensorStatus = SensorStatus.OFF;
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
    public void requestFloor(int requestedFloor) {
        System.out.println();
        System.out.println(" >> Someone inside the elevator" + this.id + " wants to go to floor <" + requestedFloor + "> <<");
        this.checkWeight();
        this.checkObstruction();
        this.moveElevator(requestedFloor);
    }
}


//------------------------------------------- DOOR CLASS --------------------------------------------------------------------------
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


//------------------------------------------- BUTTON CLASS ------------------------------------------------------------------------
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


//------------------------------------------- ENUMS -------------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------------------------
/* ******* COLUMN STATUS ******* */
enum BatteryStatus {
    ACTIVE,
    INACTIVE
}

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
    //------------------------------------------- TESTING PROGRAM - SCENARIOS ---------------------------------------------------------
    //---------------------------------------------------------------------------------------------------------------------------------

    /* ******* CREATE SCENARIO 1 ******* */
    public static void scenario1() {
        Battery batteryScenario1 = new Battery(1, 4, 66, 6, 5, BatteryStatus.ACTIVE);
        System.out.println(batteryScenario1);
        batteryScenario1.columnsList.forEach(System.out::println); //batteryScenario1.columnsList.forEach(column -> System.out.println(column));
        System.out.println();
        System.out.println("****************************** SCENARIO 1: ******************************");
        System.out.println("MOVING ELEVATORS:");
        System.out.println();

//        *******************************************************************************************************************************************
        System.out.println("--------- ElevatorB1 ---------");
        batteryScenario1.columnsList.get(1).elevatorsList.get(0).floor = 20; //Elevator B1 (column2 elevator1)
        batteryScenario1.columnsList.get(1).elevatorsList.get(0).moveElevator(5); //Elevator B1 (column2 elevator1)

        System.out.println();
        System.out.println("--------- ElevatorB2 ---------");
        batteryScenario1.columnsList.get(1).elevatorsList.get(1).floor = 3; //Elevator B2 (column2 elevator2)
        batteryScenario1.columnsList.get(1).elevatorsList.get(1).moveElevator(15); //Elevator B2 (column2 elevator2)

        System.out.println();
        System.out.println("--------- ElevatorB3 ---------");
        batteryScenario1.columnsList.get(1).elevatorsList.get(2).floor = 13; //Elevator B2 (column2 elevator3)
        batteryScenario1.columnsList.get(1).elevatorsList.get(2).moveElevator(1); //Elevator B3 (column2 elevator3)

        System.out.println();
        System.out.println("--------- ElevatorB4 ---------");
        batteryScenario1.columnsList.get(1).elevatorsList.get(3).floor = 15; //Elevator B2 (column2 elevator4)
        batteryScenario1.columnsList.get(1).elevatorsList.get(3).moveElevator(2); //Elevator B4 (column2 elevator4)

        System.out.println();
        System.out.println("--------- ElevatorB5 ---------");
        batteryScenario1.columnsList.get(1).elevatorsList.get(4).floor = 6; //Elevator B2 (column2 elevator5)
        batteryScenario1.columnsList.get(1).elevatorsList.get(4).moveElevator(1); //Elevator B5 (column2 elevator5)


//        *******************************************************************************************************************************************
//        ********************************* SCENARIO1 - SECOND OPTION *******************************************************************************
//        *******************************************************************************************************************************************

//        batteryScenario1.columnsList.get(1).elevatorsList.get(0).floor = 5; //Elevator B1 (column2 elevator1)
//        batteryScenario1.columnsList.get(1).elevatorsList.get(1).floor = 15; //Elevator B2 (column2 elevator2)
//        batteryScenario1.columnsList.get(1).elevatorsList.get(2).floor = 1; //Elevator B2 (column2 elevator3)
//        batteryScenario1.columnsList.get(1).elevatorsList.get(3).floor = 2; //Elevator B2 (column2 elevator4)
//        batteryScenario1.columnsList.get(1).elevatorsList.get(4).floor = 1; //Elevator B2 (column2 elevator5)
//        *******************************************************************************************************************************************


        System.out.println();
        System.out.println("---------------------------------------------------------------------");
        System.out.println("---------------------------------------------------------------------");
        batteryScenario1.columnsList.get(1).elevatorsList.forEach(System.out::println);
        System.out.println();
        System.out.println("Person 1: (elevator B5 is expected)"); //elevator expected
        batteryScenario1.columnsList.get(1).requestElevator(1, Direction.UP); //parameters (requestedFloor, buttonDirection.UP/DOWN)
        batteryScenario1.columnsList.get(1).elevatorsList.get(4).requestFloor(20); //parameters (requestedFloor)
        System.out.println("=====================================================================");
    }

    /* ******* CREATE SCENARIO 2 ******* */
    public static void scenario2() {
        Battery batteryScenario2 = new Battery(1, 4, 66, 6, 5, BatteryStatus.ACTIVE);
        System.out.println(batteryScenario2);
        batteryScenario2.columnsList.forEach(System.out::println); //batteryScenario2.columnsList.forEach(column -> System.out.println(column));
        System.out.println();
        System.out.println("****************************** SCENARIO 1: ******************************");
        System.out.println("MOVING ELEVATORS:");
        System.out.println();
        System.out.println("--------- ElevatorC1 ---------");
        batteryScenario2.columnsList.get(2).elevatorsList.get(0).floor = 1;
//        batteryScenario2.columnsList.get(2).elevatorsList.get(0).moveElevator(21); //not departed yet

        System.out.println();
        System.out.println("--------- ElevatorC2 ---------");
        batteryScenario2.columnsList.get(2).elevatorsList.get(1).floor = 23;
        batteryScenario2.columnsList.get(2).elevatorsList.get(1).moveElevator(28);

        System.out.println();
        System.out.println("--------- ElevatorC3 ---------");
        batteryScenario2.columnsList.get(2).elevatorsList.get(2).floor = 33;
        batteryScenario2.columnsList.get(2).elevatorsList.get(2).moveElevator(1);

        System.out.println();
        System.out.println("--------- ElevatorC4 ---------");
        batteryScenario2.columnsList.get(2).elevatorsList.get(3).floor = 40;
        batteryScenario2.columnsList.get(2).elevatorsList.get(3).moveElevator(24);

        System.out.println();
        System.out.println("--------- ElevatorC5 ---------");
        batteryScenario2.columnsList.get(2).elevatorsList.get(4).floor = 39;
        batteryScenario2.columnsList.get(2).elevatorsList.get(4).moveElevator(1);

        System.out.println();
        System.out.println("---------------------------------------------------------------------");
        System.out.println("---------------------------------------------------------------------");
        batteryScenario2.columnsList.get(2).elevatorsList.forEach(System.out::println);
        System.out.println();
        System.out.println("Person 1: (elevator C1 is expected)"); //elevator expected
        batteryScenario2.columnsList.get(2).requestElevator(1, Direction.UP); //parameters (requestedFloor, buttonDirection.UP/DOWN)
        batteryScenario2.columnsList.get(2).elevatorsList.get(0).requestFloor(36); //parameters (requestedFloor)
        System.out.println("=====================================================================");
    }

    /* ******* CREATE SCENARIO 3 ******* */
    public static void scenario3() {

    }


    //------------------------------------------- TEST YOUR SCENARIO ------------------------------------------------------------------
    //---------------------------------------------------------------------------------------------------------------------------------
    //  Instruction for your test:
    // 1- Uncomment the scenarioX() function
    // 2- Change the 'X' for a value (see the notes to fill correctly at the comments at right of each line)
    // 3- Uncomment the 'scenarioX()' at the end of the file
    // 4- Run the code using a terminal of your preference. Before you need to compile the file by typing: javac Residential_Controller.java
    //    Then it will generate a new file and you can run called Residential_Controller.class and now you can run the program by typing: java Residential_Controller
    //    Or you can just run the program using an IDE (integrated development environment) like IntelliJ IDEA, Eclipse, NetBeans, etc

//    public static void scenarioX () {
//        System.out.println();
//        System.out.println("****************************** SCENARIO X: ******************************");
//        Column columnX = new Column(X, ColumnStatus.X, X, X); //set parameters (id, ColumnStatus.ACTIVE/INACTIVE, numberOfFloors, numberOfElevators)
//        columnX.elevatorsList.get(0).floor = X; //floor where the elevator 1 is
//        columnX.elevatorsList.get(1).floor = X; //floor where the elevator 2 is
//        // If you have more than 2 elevators, make a copy of the line above and put the corresponding index inside the parenthesis .get(X)
//
//        System.out.println();
//        System.out.println("Person X: (elevator X is expected)"); //elevator expected
//        columnX.requestElevator(X, Direction.X); //set parameters (requestedFloor, Direction.UP/DOWN)
//        columnX.elevatorsList.get(X).requestFloor(X); //choose elevator by index and set parameters (requestedFloor)
//        System.out.println("==================================");
//    }


    public static void main(String[] args) {
        //------------------------------------------- TESTING PROGRAM - CALL SCENARIOS -----------------------------------------------------
        //----------------------------------------------------------------------------------------------------------------------------------
        /* ******* CALL SCENARIOS ******* */
        scenario1();
//        scenario2();
//        scenario3();
//        scenario4();

        /* ******* CALL YOUR SCENARIO ******* */
        // scenarioX(maxWeight)
    }
}
