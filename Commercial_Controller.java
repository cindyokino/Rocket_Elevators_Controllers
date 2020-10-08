/* *********************************************** **
 @Author		Cindy Okino
 @Website		https://github.com/cindyokino
 @Last Update	October 9, 2020


 SUMMARY:
 0- BATTERY CLASS
    0a- Constructor and its attributes
    0b- Method toString
    0c- Methods to create a list: createColumnsList, createListsInsideColumns
    0d- Methods for logic: calculateNumberOfFloorsPerColumn, setColumnValues, initializeBasementColumnFloors, initializeMultiColumnFloors, initializeUniqueColumnFloors
 1- COLUMN CLASS
    1a- Constructor and its attributes
    1b- Method toString
    1c- Methods to create a list: createElevatorsList, createButtonsUpList, createButtonsDownList
    1d- Methods for logic: findElevator, findNearestElevator, manageButtonStatusOn
    1e- Entry method: requestElevator
 2- ELEVATOR CLASS
    2a- Constructor and its attributes
    2b- Method toString
    2c- Methods to create a list: createFloorDoorsList, createDisplaysList, createFloorButtonsList, addFloorToFloorList
    2d- Methods for logic: moveElevator, moveUp, moveDown, manageButtonStatusOff, updateDisplays, openDoors, closeDoors, checkWeight, checkObstruction, addFloorToFloorList, deleteFloorFromList
    2e- Entry method: requestFloor
 3- DOOR CLASS
 4- BUTTON CLASS
 5- DISPLAY CLASS
 6- ENUMS
 7- TESTING PROGRAM - SCENARIOS
 9- TESTING PROGRAM - CALL SCENARIOS

 CONTROLLED OBJECTS:
 Battery: contains a list of N columns
 Columns: controls a list of N elevators
 Elevators: controls doors, buttons, displays

 numberOfBasements                                                  
 numberOfFloors                                                     //Floors of the building excluding the number of basements
 totalNumberOfFloors = numberOfFloors + Math.abs(numberOfBasements) //Transform the number of basements to a positive number
 minBuildingFloor                                                   //Is equal to 1 OR equal the numberOfBasements if there is a basement
 maxBuildingFloor = numberOfFloors                                  //Is the last floor of the building
 maxWeight                                                          //Maximum weight an elevator can carry in KG

  ** ************************************************** */


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
    int totalNumberOfFloors;               //numberOfFloors + Math.abs(numberOfBasements)
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
            this.columnsList.add(new Column(i, name, ColumnStatus.ACTIVE, this.numberOfElevatorsPerColumn, numberOfFloorsPerColumn, numberOfBasements, this));
            name += 1;
        }
    }

    /* ******* CREATE A LIST OF COLUMNS FOR THE BATTERY ******* */
    public void createListsInsideColumns() {
        columnsList.forEach(column -> {
            column.createElevatorsList();
            column.createButtonsUpList();
            column.createButtonsDownList();
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
        } else { //if there is no basement
            remainingFloors = this.numberOfFloors % this.numberOfColumns;
        }

        //setting the minFloor and maxFloor of each column
        if (this.numberOfColumns == 1) { //if there is just one column, it serves all the floors of the building
            initializeUniqueColumnFloors();
        } else { //for more than 1 column
            initializeMultiColumnFloors();

            //adjusting the number of served floors of the columns if there are remaining floors
            if (remainingFloors != 0) { //if the remainingFloors is not zero, then it adds the remaining floors to the last column
                this.columnsList.get(this.columnsList.size() - 1).numberServedFloors = numberOfFloorsPerColumn + remainingFloors;
                this.columnsList.get(this.columnsList.size() - 1).maxFloor = this.columnsList.get(this.columnsList.size() - 1).minFloor + this.columnsList.get(this.columnsList.size() - 1).numberServedFloors;
            }
            //if there is a basement, then the first column will serve the basements + RDC
            if (this.numberOfBasements > 0) {
                initializeBasementColumnFloors();
            }
        }
    }

    /* ******* LOGIC TO SET THE minFloor AND maxFloor FOR THE BASEMENT COLUMN ******* */
    private void initializeBasementColumnFloors() {
        this.columnsList.get(0).numberServedFloors = (this.numberOfBasements + 1); //+1 is the RDC
        this.columnsList.get(0).minFloor = numberOfBasements * -1; //the minFloor of basement is a negative number
        this.columnsList.get(0).maxFloor = 1; //1 is the RDC
    }

    /* ******* LOGIC TO SET THE minFloor AND maxFloor FOR ALL THE COLUMNS EXCLUDING BASEMENT COLUMN ******* */
    private void initializeMultiColumnFloors() {
        int minimumFloor = 1;
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
    }

    /* ******* LOGIC TO SET THE minFloor AND maxFloor IF THERE IS JUST ONE COLUMN ******* */
    private void initializeUniqueColumnFloors() {
        int minimumFloor = 1;
        this.columnsList.get(0).numberServedFloors = totalNumberOfFloors;
        if (numberOfBasements > 0) { //if there is basement
            this.columnsList.get(0).minFloor = numberOfBasements;
        } else { //if there is NO basement
            this.columnsList.get(0).minFloor = minimumFloor;
            this.columnsList.get(0).maxFloor = numberOfFloors;
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
        this.numberOfBasements = numberOfBasements * -1;
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
    public void createButtonsDownList() {
        buttonsDownList.add(new Button(1, ButtonStatus.OFF, 1));
        int minBuildingFloor;
        if (numberOfBasements > 0) {
            minBuildingFloor = numberOfBasements * -1;
        } else {
            minBuildingFloor = 1;
        }
        for (int i = (minBuildingFloor + 1); i <= this.maxFloor; i++) {
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
            bestElevator = this.findNearestElevator(currentFloor, sameDirectionElevatorList); // 1- Try to use an elevator that is moving and has the same direction
        } else if (idleElevatorList.size() > 0){
            bestElevator = this.findNearestElevator(currentFloor, idleElevatorList); // 2- Try to use an elevator that is IDLE
        } else {
            bestElevator = this.findNearestElevator(currentFloor, activeElevatorList); // 3- As the last option, uses an elevator that is moving at the contrary direction
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

    /* ******* LOGIC TO TURN ON THE BUTTONS FOR THE ASKED DIRECTION ******* */
    private void manageButtonStatusOn(int requestedFloor, Direction direction) {
        if (direction == Direction.UP) {
            //find the UP button by ID
            Optional<Button> currentButton = this.buttonsUpList.stream().filter(door -> door.id == requestedFloor).findFirst();
            if (currentButton.isPresent()) {
                currentButton.get().status = ButtonStatus.ON;
            }
        } else {
            //find the DOWN button by ID
            Optional<Button> currentButton = this.buttonsDownList.stream().filter(door -> door.id == requestedFloor).findFirst();
            if (currentButton.isPresent()) {
                currentButton.get().status = ButtonStatus.ON;
            }
        }
    }


    //----------------- Entry method -----------------//
    /* ******* ENTRY METHOD ******* */
    /* ******* REQUEST FOR AN ELEVATOR BY PRESSING THE UP OU DOWN BUTTON OUTSIDE THE ELEVATOR ******* */
    public void requestElevator(int requestedFloor, Direction direction) { // User goes to the specific column and press a button outside the elevator requesting for an elevator
        manageButtonStatusOn(requestedFloor, direction); //turn ON the good button
//        System.out.println(">> Someone request an elevator from floor <" + requestedFloor + "> and direction <" + direction + "> <<");
        Elevator bestElevator = this.findElevator(requestedFloor, direction);
        if (bestElevator.floor != requestedFloor) {
            bestElevator.addFloorToFloorList(requestedFloor);
            bestElevator.moveElevator(requestedFloor);
        }
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
        return "elevator" + column.name + this.id + " | Floor: " + this.floor + " | Status: " + this.status;
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

        while (this.floorList.size() > 0) {
            if (this.status == ElevatorStatus.IDLE) {
                if (this.floor < requestedFloor) {
                    this.status = ElevatorStatus.UP;
                } else if (this.floor > requestedFloor) {
                    this.status = ElevatorStatus.DOWN;
                } else {
                    this.openDoors();
                    this.deleteFloorFromList(requestedFloor);

                    // finding buttons by ID using Optional
                    Optional<Button> currentUpButton = this.column.buttonsUpList.stream().filter(button -> button.id == requestedFloor).findFirst();
                    if (currentUpButton.isPresent()) {
                        currentUpButton.get().status = ButtonStatus.OFF;
                    }
                    Optional<Button> currentDownButton = this.column.buttonsDownList.stream().filter(button -> button.id == requestedFloor).findFirst();
                    if (currentDownButton.isPresent()) {
                        currentDownButton.get().status = ButtonStatus.OFF;
                    }
                    Optional<Button> currentFloorButton = this.floorButtonsList.stream().filter(button -> button.id == requestedFloor).findFirst();
                    if (currentFloorButton.isPresent()) {
                        currentFloorButton.get().status = ButtonStatus.OFF;
                    }

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
                this.manageButtonStatusOff(nextFloor);
            }
        }
        if (this.floorList.size() == 0) {
            this.status = ElevatorStatus.IDLE;
//            System.out.println("       Elevator" + column.name + this.id + " is now " + this.status);
        } else {
            this.status = ElevatorStatus.DOWN;
//            System.out.println("       Elevator" + column.name + this.id + " is now going " + this.status);
        }
    }

    /* ******* LOGIC TO MOVE DOWN ******* */
    public void moveDown() {
        List<Integer> tempArray = new ArrayList<>(this.floorList);
        for (int i = this.floor; i > tempArray.get(tempArray.size() - 1); i--) {
            // finding doors by id
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
                this.manageButtonStatusOff(nextFloor);
            }
        }
        if (this.floorList.size() == 0) {
            this.status = ElevatorStatus.IDLE;
//            System.out.println("       Elevator" + column.name + this.id + " is now " + this.status);
        } else {
            this.status = ElevatorStatus.UP;
//            System.out.println("       Elevator" + column.name + this.id + " is now going " + this.status);
        }
    }

    /* ******* LOGIC TO FIND BUTTONS BY ID AND SET BUTTON STATUS OFF ******* */
    private void manageButtonStatusOff(int nextFloor) {
        Optional<Button> currentUpButton = this.column.buttonsUpList.stream().filter(button -> button.id == nextFloor).findFirst(); //filter UP button by ID and set status to OFF
        if (currentUpButton.isPresent()) {
            currentUpButton.get().status = ButtonStatus.OFF;
        }
        Optional<Button> currentFloorButton = this.floorButtonsList.stream().filter(button -> button.id == nextFloor).findFirst(); //filter floor button by ID and set status to OFF
        if (currentFloorButton.isPresent()) {
            currentFloorButton.get().status = ButtonStatus.OFF;
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
        System.out.println("       Elevator is stopped at floor " + this.floor);
        System.out.println("       Opening doors...");
        System.out.println("       Elevator doors are opened");
        this.elevatorDoor.status = DoorStatus.OPENED;
        Optional<Door> currentDoor = this.floorDoorsList.stream().filter(door -> door.id == this.floor).findFirst(); //filter floor door by ID and set status to OPENED
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
        this.checkWeight();
        this.checkObstruction();
        if (this.weightSensorStatus == SensorStatus.OFF && this.obstructionSensorStatus == SensorStatus.OFF) { //Security logic
            System.out.println("       Closing doors...");
            System.out.println("       Elevator doors are closed");
            Optional<Door> currentDoor = this.floorDoorsList.stream().filter(door -> door.id == this.floor).findFirst(); //filter floor door by ID and set status to OPENED
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
        if (!floorList.contains(floor)) {
            this.floorList.add(floor);
            Collections.sort(this.floorList);
        }
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
//        System.out.println(" >> Someone inside the elevator" + this.id + " wants to go to floor <" + requestedFloor + "> <<");
        if (this.floor != requestedFloor) {
            this.addFloorToFloorList(requestedFloor);
            this.moveElevator(requestedFloor);
        }
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
/* ******* BATTERY STATUS ******* */
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


//------------------------------------------- TESTING PROGRAM - SCENARIOS ---------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------------------------
public class Commercial_Controller {

    /* ******* CREATE SCENARIO 1 ******* */
    public static void scenario1() {
        System.out.println("\n****************************** SCENARIO 1: ***************************\n");
        Battery batteryScenario1 = new Battery(1, 4, 66, 6, 5, BatteryStatus.ACTIVE);
        System.out.println(batteryScenario1);
        System.out.println();
        batteryScenario1.columnsList.forEach(System.out::println); //batteryScenario1.columnsList.forEach(column -> System.out.println(column));
        System.out.println();
        //--------- ElevatorB1 ---------
        batteryScenario1.columnsList.get(1).elevatorsList.get(0).floor = 20;
        batteryScenario1.columnsList.get(1).elevatorsList.get(0).status = ElevatorStatus.DOWN;
        batteryScenario1.columnsList.get(1).elevatorsList.get(0).addFloorToFloorList(5);

        //--------- ElevatorB2 ---------
        batteryScenario1.columnsList.get(1).elevatorsList.get(1).floor = 3;
        batteryScenario1.columnsList.get(1).elevatorsList.get(1).status = ElevatorStatus.UP;
        batteryScenario1.columnsList.get(1).elevatorsList.get(1).addFloorToFloorList(15);

        //--------- ElevatorB3 ---------
        batteryScenario1.columnsList.get(1).elevatorsList.get(2).floor = 13;
        batteryScenario1.columnsList.get(1).elevatorsList.get(2).status = ElevatorStatus.DOWN;
        batteryScenario1.columnsList.get(1).elevatorsList.get(2).addFloorToFloorList(1);

        //--------- ElevatorB4 ---------
        batteryScenario1.columnsList.get(1).elevatorsList.get(3).floor = 15;
        batteryScenario1.columnsList.get(1).elevatorsList.get(3).status = ElevatorStatus.DOWN;
        batteryScenario1.columnsList.get(1).elevatorsList.get(3).addFloorToFloorList(2);

        //--------- ElevatorB5 ---------
        batteryScenario1.columnsList.get(1).elevatorsList.get(4).floor = 6;
        batteryScenario1.columnsList.get(1).elevatorsList.get(4).status = ElevatorStatus.DOWN;
        batteryScenario1.columnsList.get(1).elevatorsList.get(4).addFloorToFloorList(1);

        batteryScenario1.columnsList.get(1).elevatorsList.forEach(System.out::println);
        System.out.println();
        System.out.println("Person 1: (elevator B5 is expected)"); //elevator expected
        System.out.println(">> User request an elevator from floor <1> and direction <UP> <<");
        System.out.println(">> User request to go to floor <20>");
        batteryScenario1.columnsList.get(1).requestElevator(1, Direction.UP); //parameters (requestedFloor, buttonDirection.UP/DOWN)
        batteryScenario1.columnsList.get(1).elevatorsList.get(4).requestFloor(20); //parameters (requestedFloor)
        System.out.println("=========================================================================");
    }

    /* ******* CREATE SCENARIO 2 ******* */
    public static void scenario2() {
        System.out.println("\n****************************** SCENARIO 2: ******************************\n");
        Battery batteryScenario2 = new Battery(1, 4, 66, 6, 5, BatteryStatus.ACTIVE);
        System.out.println(batteryScenario2);
        System.out.println();
        batteryScenario2.columnsList.forEach(System.out::println);
        System.out.println();
        //--------- ElevatorC1 ---------
        batteryScenario2.columnsList.get(2).elevatorsList.get(0).floor = 1;
        batteryScenario2.columnsList.get(2).elevatorsList.get(0).status = ElevatorStatus.UP;
        batteryScenario2.columnsList.get(2).elevatorsList.get(0).addFloorToFloorList(21); //not departed yet

        //--------- ElevatorC2 ---------
        batteryScenario2.columnsList.get(2).elevatorsList.get(1).floor = 23;
        batteryScenario2.columnsList.get(2).elevatorsList.get(1).status = ElevatorStatus.UP;
        batteryScenario2.columnsList.get(2).elevatorsList.get(1).addFloorToFloorList(28);

        //--------- ElevatorC3 ---------
        batteryScenario2.columnsList.get(2).elevatorsList.get(2).floor = 33;
        batteryScenario2.columnsList.get(2).elevatorsList.get(2).status = ElevatorStatus.DOWN;
        batteryScenario2.columnsList.get(2).elevatorsList.get(2).addFloorToFloorList(1);

        //--------- ElevatorC4 ---------
        batteryScenario2.columnsList.get(2).elevatorsList.get(3).floor = 40;
        batteryScenario2.columnsList.get(2).elevatorsList.get(3).status = ElevatorStatus.DOWN;
        batteryScenario2.columnsList.get(2).elevatorsList.get(3).addFloorToFloorList(24);

        //--------- ElevatorC5 ---------
        batteryScenario2.columnsList.get(2).elevatorsList.get(4).floor = 39;
        batteryScenario2.columnsList.get(2).elevatorsList.get(4).status = ElevatorStatus.DOWN;
        batteryScenario2.columnsList.get(2).elevatorsList.get(4).addFloorToFloorList(1);

        batteryScenario2.columnsList.get(2).elevatorsList.forEach(System.out::println);
        System.out.println();
        System.out.println("Person 1: (elevator C1 is expected)"); //elevator expected
        System.out.println(">> User request an elevator from floor <1> and direction <UP> <<");
        System.out.println(">> User request to go to floor <36>");
        batteryScenario2.columnsList.get(2).requestElevator(1, Direction.UP); //parameters (requestedFloor, buttonDirection.UP/DOWN)
        batteryScenario2.columnsList.get(2).elevatorsList.get(0).requestFloor(36); //parameters (requestedFloor)
        System.out.println("=========================================================================");
    }

    /* ******* CREATE SCENARIO 3 ******* */
    public static void scenario3() {
        System.out.println("\n****************************** SCENARIO 3: ******************************\n");
        Battery batteryScenario3 = new Battery(1, 4, 66, 6, 5, BatteryStatus.ACTIVE);
        System.out.println(batteryScenario3);
        System.out.println();
        batteryScenario3.columnsList.forEach(System.out::println);
        System.out.println();
        //--------- ElevatorD1 ---------
        batteryScenario3.columnsList.get(3).elevatorsList.get(0).floor = 58;
        batteryScenario3.columnsList.get(3).elevatorsList.get(0).status = ElevatorStatus.DOWN;
        batteryScenario3.columnsList.get(3).elevatorsList.get(0).addFloorToFloorList(1);

        //--------- ElevatorD2 ---------
        batteryScenario3.columnsList.get(3).elevatorsList.get(1).floor = 50;
        batteryScenario3.columnsList.get(3).elevatorsList.get(1).status = ElevatorStatus.UP;
        batteryScenario3.columnsList.get(3).elevatorsList.get(1).addFloorToFloorList(60);

        //--------- ElevatorD3 ---------
        batteryScenario3.columnsList.get(3).elevatorsList.get(2).floor = 46;
        batteryScenario3.columnsList.get(3).elevatorsList.get(2).status = ElevatorStatus.UP;
        batteryScenario3.columnsList.get(3).elevatorsList.get(2).addFloorToFloorList(58);

        //--------- ElevatorD4 ---------
        batteryScenario3.columnsList.get(3).elevatorsList.get(3).floor = 1;
        batteryScenario3.columnsList.get(3).elevatorsList.get(3).status = ElevatorStatus.UP;
        batteryScenario3.columnsList.get(3).elevatorsList.get(3).addFloorToFloorList(54);

        //--------- ElevatorD5 ---------
        batteryScenario3.columnsList.get(3).elevatorsList.get(4).floor = 60;
        batteryScenario3.columnsList.get(3).elevatorsList.get(4).status = ElevatorStatus.DOWN;
        batteryScenario3.columnsList.get(3).elevatorsList.get(4).addFloorToFloorList(1);

        batteryScenario3.columnsList.get(3).elevatorsList.forEach(System.out::println);
        System.out.println();
        System.out.println("Person 1: (elevator D1 is expected)"); //elevator expected
        System.out.println(">> User request an elevator from floor <54> and direction <DOWN> <<");
        System.out.println(">> User request to go to floor <1>");
        batteryScenario3.columnsList.get(3).requestElevator(54, Direction.DOWN); //parameters (requestedFloor, buttonDirection.UP/DOWN)
        batteryScenario3.columnsList.get(3).elevatorsList.get(0).requestFloor(1); //parameters (requestedFloor)
        System.out.println("=========================================================================");
    }

    /* ******* CREATE SCENARIO 4 ******* */
    public static void scenario4() { 
        System.out.println("\n****************************** SCENARIO 4: ******************************\n");
        Battery batteryScenario4 = new Battery(1, 4, 66, 6, 5, BatteryStatus.ACTIVE);
        System.out.println(batteryScenario4);
        System.out.println();
        batteryScenario4.columnsList.forEach(System.out::println);
        System.out.println();
        //--------- ElevatorA1 ---------
        batteryScenario4.columnsList.get(0).elevatorsList.get(0).floor = -4; //use of negative numbers to indicate SS / basement
        batteryScenario4.columnsList.get(0).elevatorsList.get(0).status = ElevatorStatus.IDLE;

        //--------- ElevatorA2 ---------
        batteryScenario4.columnsList.get(0).elevatorsList.get(1).floor = 1;
        batteryScenario4.columnsList.get(0).elevatorsList.get(1).status = ElevatorStatus.IDLE;

        //--------- ElevatorA3 ---------
        batteryScenario4.columnsList.get(0).elevatorsList.get(2).floor = -3; //use of negative numbers to indicate SS / basement
        batteryScenario4.columnsList.get(0).elevatorsList.get(2).status = ElevatorStatus.DOWN;
        batteryScenario4.columnsList.get(0).elevatorsList.get(2).addFloorToFloorList(-5);

        //--------- ElevatorA4 ---------
        batteryScenario4.columnsList.get(0).elevatorsList.get(3).floor = -6; //use of negative numbers to indicate SS / basement
        batteryScenario4.columnsList.get(0).elevatorsList.get(3).status = ElevatorStatus.UP;
        batteryScenario4.columnsList.get(0).elevatorsList.get(3).addFloorToFloorList(1);

        //--------- ElevatorA5 ---------
        batteryScenario4.columnsList.get(0).elevatorsList.get(4).floor = -1; //use of negative numbers to indicate SS / basement
        batteryScenario4.columnsList.get(0).elevatorsList.get(4).status = ElevatorStatus.DOWN;
        batteryScenario4.columnsList.get(0).elevatorsList.get(4).addFloorToFloorList(-6);

        batteryScenario4.columnsList.get(0).elevatorsList.forEach(System.out::println);
        System.out.println();
        System.out.println("Person 1: (elevator A4 is expected)"); //elevator expected
        System.out.println(">> User request an elevator from floor <-3> (basement) and direction <UP> <<");
        System.out.println(">> User request to go to floor <1>");
        batteryScenario4.columnsList.get(0).requestElevator(-3, Direction.UP); //parameters (requestedFloor, buttonDirection.UP/DOWN)
        batteryScenario4.columnsList.get(0).elevatorsList.get(3).requestFloor(1); //parameters (requestedFloor)
        System.out.println("=========================================================================");
    }
    

    //------------------------------------------- TESTING PROGRAM - CALL SCENARIOS -----------------------------------------------------
    //----------------------------------------------------------------------------------------------------------------------------------
    public static void main(String[] args) {
        /* ******* CALL SCENARIOS ******* */
        scenario1();
        scenario2();
        scenario3();
        scenario4();
    }
}