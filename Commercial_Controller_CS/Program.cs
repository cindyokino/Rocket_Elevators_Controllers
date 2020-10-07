/* *********************************************** **
 @Author		Cindy Okino
 @Website		https://github.com/cindyokino
 @Last Update	October 9, 2020


 SUMMARY:
 0- BATTERY CLASS
    0a- Constructor and its attributes
    0b- Method toString
    0c- Methods to create a list: createColumnsList, createListsInsideColumns
    0d- Methods for logic: calculateNumberOfFloorsPerColumn, setColumnValues
 1- COLUMN CLASS
    1a- Constructor and its attributes
    1b- Method toString
    1c- Methods to create a list: createElevatorsList, createButtonsUpList, createButtonsDownList
    1d- Methods for logic: optimizeDisplacement, findElevator, findNearestElevator
    1e- Entry method: requestElevator
 2- ELEVATOR CLASS
    2a- Constructor and its attributes
    2b- Method toString
    2c- Methods to create a list: createFloorDoorsList, createDisplaysList, createFloorButtonsList, addFloorToFloorList
    2d- Methods for logic: moveElevator, moveUp, moveDown, updateDisplays, openDoors, closeDoors, checkWeight, checkObstruction, addFloorToFloorList, deleteFloorFromList
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

 numberOfBasements                                                  //Use a negative number
 numberOfFloors                                                     //Floors of the building excluding the number of basements
 totalNumberOfFloors = numberOfFloors + Math.abs(numberOfBasements) //Transform the number of basements to a positive number
 minBuildingFloor                                                   //Is equal to 1 OR equal the numberOfBasements if there is a basement
 maxBuildingFloor = numberOfFloors                                  //Is the last floor of the building
 maxWeight                                                          //Maximum weight an elevator can carry in KG

  ** ************************************************** */


using System;
using System.Linq;
using System.Collections.Generic;

namespace Commercial_Controller_CS
{
    //------------------------------------------- BATTERY CLASS -----------------------------------------------------------------------
    //---------------------------------------------------------------------------------------------------------------------------------
    class Battery
    {
        public int id;
        public int numberOfColumns;
        public int minBuildingFloor;                  //Is equal to 1 OR equal the numberOfBasements if there is a basement
        public int maxBuildingFloor;                  //Is the last floor of the building
        public int numberOfFloors;                    //Floors of the building excluding the number of basements
        public int numberOfBasements;                 //Is a negative number
        public int totalNumberOfFloors;               //numberOfFloors + Math.abs(numberOfBasements)
        public int numberOfElevatorsPerColumn;
        public int numberOfFloorsPerColumn;
        public BatteryStatus status;
        public List<Column> columnsList;

        //----------------- Constructor and its attributes -----------------//
        public Battery(int batteryId, int batteryNumberOfColumns, int batteryTotalNumberOfFloors, int batteryNumberOfBasements, int batteryNumberOfElevatorsPerColumn, BatteryStatus batteryStatus)
        {
            id = batteryId;
            numberOfColumns = batteryNumberOfColumns;
            totalNumberOfFloors = batteryTotalNumberOfFloors;
            numberOfBasements = batteryNumberOfBasements;
            numberOfElevatorsPerColumn = batteryNumberOfElevatorsPerColumn;
            status = batteryStatus;
            columnsList = new List<Column>();
            // numberOfFloorsPerColumn = calculateNumberOfFloorsPerColumn();
            createColumnsList();
            setColumnValues();
            createListsInsideColumns();
        }


        //----------------- Method toString -----------------//
        /* ******* GET A STRING REPRESENTATION OF BATTERY OBJECT ******* */
        public override string ToString()
        {
            return "battery" + this.id + " | Basements: " + this.numberOfBasements + " | Columns: " + this.numberOfColumns + " | Elevators per column: " + this.numberOfElevatorsPerColumn;
        }


        //----------------- Methods to create a list -----------------//
        /* ******* CREATE A LIST OF COLUMNS FOR THE BATTERY ******* */
        public void createColumnsList()
        {
            char name = 'A';
            for (int i = 1; i <= this.numberOfColumns; i++)
            {
                this.columnsList.Add(new Column(i, name, ColumnStatus.ACTIVE, this.numberOfElevatorsPerColumn, numberOfFloorsPerColumn, numberOfBasements, this));
                System.Console.WriteLine("column" + name + " created!!!");
                name = Convert.ToChar(name + 1);
            }
        }

        /* ******* CREATE A LIST OF COLUMNS FOR THE BATTERY ******* */
        public void createListsInsideColumns()
        {
            foreach (Column column in columnsList)
            {
                column.createElevatorsList();
                column.createButtonsUpList();
                column.createButtonsDownList(numberOfBasements);
            }
        }


        //----------------- Methods for logic -----------------//
        /* ******* LOGIC TO FIND THE FLOORS SERVED PER EACH COLUMN ******* */
        public int calculateNumberOfFloorsPerColumn()
        {
            numberOfFloors = totalNumberOfFloors + numberOfBasements; //numberOfBasements is negative
            int numberOfFloorsPerColumn;

            if (this.numberOfBasements > 0)
            { //if there is basement floors
                numberOfFloorsPerColumn = (this.numberOfFloors / (this.numberOfColumns - 1)); //the first column serves the basement floors
            }
            else
            { //there is no basement
                numberOfFloorsPerColumn = (this.numberOfFloors / this.numberOfColumns);
            }

            return numberOfFloorsPerColumn;
        }

        /* ******* LOGIC TO FIND THE REMAINING FLOORS OF EACH COLUMN AND SET VALUES servedFloors, minFloors, maxFloors ******* */
        public void setColumnValues()
        {
            int remainingFloors;

            //calculating the remaining floors
            if (this.numberOfBasements > 0)
            { //if there are basement floors
                remainingFloors = this.numberOfFloors % (this.numberOfColumns - 1);
            }
            else
            { //there is no basement
                remainingFloors = this.numberOfFloors % this.numberOfColumns;
            }

            //setting the minFloor and maxFloor of each column
            int minimumFloor = 1;
            if (this.numberOfColumns == 1)
            { //if there is just one column, it serves all the floors of the building
                this.columnsList[0].numberServedFloors = totalNumberOfFloors;
                if (numberOfBasements > 0)
                { //if there is basement
                    this.columnsList[0].minFloor = numberOfBasements;
                }
                else
                { //if there is NO basement
                    this.columnsList[0].minFloor = minimumFloor;
                    this.columnsList[0].maxFloor = numberOfFloors;
                }
            }
            else
            { //for more than 1 column
                for (int i = 1; i < this.columnsList.Count; i++)
                { //if its not the first column (because the first column serves the basements)
                    if (i == 1)
                    {
                        this.columnsList[i].numberServedFloors = numberOfFloorsPerColumn;
                    }
                    else
                    {
                        this.columnsList[i].numberServedFloors = (numberOfFloorsPerColumn + 1); //Add 1 floor for the RDC/ground floor
                    }
                    this.columnsList[i].minFloor = minimumFloor;
                    this.columnsList[i].maxFloor = (this.columnsList[i].minFloor + numberOfFloorsPerColumn - 1);
                    minimumFloor = this.columnsList[i].maxFloor + 1; //setting the minimum floor for the next column
                }

                //adjusting the number of served floors of the columns if there are remaining floors
                if (remainingFloors != 0)
                { //if the remainingFloors is not zero, then it adds the remaining floors to the last column
                    this.columnsList[this.columnsList.Count - 1].maxFloor = this.columnsList[this.columnsList.Count - 1].minFloor + this.columnsList[this.columnsList.Count - 1].numberServedFloors;
                    this.columnsList[this.columnsList.Count - 1].numberServedFloors = numberOfFloorsPerColumn + remainingFloors;
                }
                //if there is a basement, then the first column will serve the basements + RDC
                if (this.numberOfBasements > 0)
                {
                    this.columnsList[0].numberServedFloors = (this.numberOfBasements + 1); //+1 is the RDC
                    this.columnsList[0].minFloor = numberOfBasements; //the minFloor of basement is a negative number
                    this.columnsList[0].maxFloor = 1; //1 is the RDC
                }
            }
        }

    }


    //------------------------------------------- COLUMN CLASS ------------------------------------------------------------------------
    //---------------------------------------------------------------------------------------------------------------------------------
    class Column
    {
        public int id;
        public char name;
        public ColumnStatus status;
        public int numberOfElevatorsPerColumn;
        public int minFloor;
        public int maxFloor;
        public int numberServedFloors;
        public int numberOfBasements;
        public Battery battery;
        public List<Elevator> elevatorsList;
        public List<Button> buttonsUpList;
        public List<Button> buttonsDownList;

        //----------------- Constructor and its attributes -----------------//
        public Column(int columnId, char columnName, ColumnStatus columnStatus, int columnNumberOfElevators, int columnNumberServedFloors, int columnNumberOfBasements, Battery columnBattery)
        {
            id = columnId;
            name = columnName;
            status = columnStatus;
            numberOfElevatorsPerColumn = columnNumberOfElevators;
            numberServedFloors = columnNumberServedFloors;
            numberOfBasements = columnNumberOfBasements;
            battery = columnBattery;
            elevatorsList = new List<Elevator>();
            buttonsUpList = new List<Button>();
            buttonsDownList = new List<Button>();
        }


        //----------------- Method toString -----------------//
        /* ******* GET A STRING REPRESENTATION OF COLUMN OBJECT ******* */
        public override string ToString()
        {
            return "column" + this.name + " | Served floors: " + this.numberServedFloors + " | Min floor: " + this.minFloor + " | Max floor: " + this.maxFloor;
        }


        //----------------- Methods to create a list -----------------//
        /* ******* CREATE A LIST OF ELEVATORS FOR THE COLUMN ******* */
        public void createElevatorsList()
        {
            for (int i = 1; i <= this.numberOfElevatorsPerColumn; i++)
            {
                this.elevatorsList.Add(new Elevator(i, this.numberServedFloors, 1, ElevatorStatus.IDLE, SensorStatus.OFF, SensorStatus.OFF, this));
            }
        }

        /* ******* CREATE A LIST WITH UP BUTTONS FROM THE FIRST FLOOR TO THE LAST LAST BUT ONE FLOOR ******* */
        public void createButtonsUpList()
        {
            buttonsUpList.Add(new Button(1, ButtonStatus.OFF, 1));
            for (int i = minFloor; i < this.maxFloor; i++)
            {
                this.buttonsUpList.Add(new Button(i, ButtonStatus.OFF, i));
            }
        }

        /* ******* CREATE A LIST WITH DOWN BUTTONS FROM THE SECOND FLOOR TO THE LAST FLOOR ******* */
        public void createButtonsDownList(int numberOfBasements)
        {
            buttonsDownList.Add(new Button(1, ButtonStatus.OFF, 1));
            int minBuildingFloor;
            if (numberOfBasements > 0)
            {
                minBuildingFloor = numberOfBasements;
            }
            else
            {
                minBuildingFloor = 1;
            }
            for (int i = (minBuildingFloor + 1); i <= this.maxFloor; i++)
            {
                this.buttonsDownList.Add(new Button(i, ButtonStatus.OFF, i));
            }
        }


        //----------------- Methods for logic -----------------//
        /* ******* LOGIC TO OPTIMIZE THE ELEVATORS DISPLACEMENTS ******* */
        public void optimizeDisplacement(List<Elevator> elevatorsList)
        {
            DateTime dateNow = DateTime.Now;
            DateTime morningPeakStart = new DateTime(dateNow.Year, dateNow.Month, dateNow.Day, 6, 0, 0);
            DateTime morningPeakEnd = new DateTime(dateNow.Year, dateNow.Month, dateNow.Day, 10, 0, 0);
            DateTime eveningPeakStart = new DateTime(dateNow.Year, dateNow.Month, dateNow.Day, 16, 0, 0);
            DateTime eveningPeakEnd = new DateTime(dateNow.Year, dateNow.Month, dateNow.Day, 19, 0, 0);

            elevatorsList.ForEach(elevator =>
            {
                if (DateTime.Now.TimeOfDay > (morningPeakStart.TimeOfDay) && DateTime.Now.TimeOfDay < (morningPeakEnd.TimeOfDay))
                {
                    System.Console.WriteLine("Between 6 and 10 am the elevator waits at floor 1 when status is IDLE");
                    System.Console.WriteLine("Moving elevator to floor 1");
                    elevator.moveElevator(1);
                }
                else if (DateTime.Now.TimeOfDay > (eveningPeakStart.TimeOfDay) && DateTime.Now.TimeOfDay < (eveningPeakEnd.TimeOfDay))
                {
                    System.Console.WriteLine("Between 4 and 7 pm the elevator waits at the last floor when status is IDLE");
                    System.Console.WriteLine("Moving elevator to last floor of column");
                    elevator.moveElevator(this.maxFloor);
                }
            });
        }

        /* ******* LOGIC TO FIND THE BEST ELEVATOR WITH A PRIORITIZATION LOGIC ******* */
        public Elevator findElevator(int currentFloor, Direction direction)
        {
            Elevator bestElevator;
            List<Elevator> activeElevatorList = new List<Elevator>();
            List<Elevator> idleElevatorList = new List<Elevator>();
            List<Elevator> sameDirectionElevatorList = new List<Elevator>();
            this.elevatorsList.ForEach(elevator =>
            {
                if (elevator.status != ElevatorStatus.IDLE)
                {
                    //Verify if the request is on the elevators way, otherwise the elevator will just continue its way ignoring this call
                    if (elevator.status == ElevatorStatus.UP && elevator.floor <= currentFloor || elevator.status == ElevatorStatus.DOWN && elevator.floor >= currentFloor)
                    {
                        activeElevatorList.Add(elevator);
                    }
                }
                else
                {
                    idleElevatorList.Add(elevator);
                }
            });

            if (activeElevatorList.Count > 0)
            { //Create new list for elevators with same direction that the request
                sameDirectionElevatorList = activeElevatorList.Where(elevator => elevator.status.ToString().Equals(direction.ToString())).ToList();
            }

            if (sameDirectionElevatorList.Count > 0)
            {
                bestElevator = this.findNearestElevator(currentFloor, sameDirectionElevatorList); // 1- Try to use an elevator that is moving and has the same direction
            }
            else if (idleElevatorList.Count > 0)
            {
                bestElevator = this.findNearestElevator(currentFloor, idleElevatorList); // 2- Try to use an elevator that is IDLE
            }
            else
            {
                bestElevator = this.findNearestElevator(currentFloor, activeElevatorList); // 3- As the last option, uses an elevator that is moving at the contrary direction
            }

            return bestElevator;
        }

        /* ******* LOGIC TO FIND THE NEAREST ELEVATOR ******* */
        public Elevator findNearestElevator(int currentFloor, List<Elevator> selectedList)
        {
            Elevator bestElevator = selectedList[0];
            int bestDistance = Math.Abs(selectedList[0].floor - currentFloor); //Math.abs() returns the absolute value of a number (always positive).
            foreach (Elevator elevator in selectedList)
            {
                if (Math.Abs(elevator.floor - currentFloor) < bestDistance)
                {
                    bestElevator = elevator;
                }
            }
            System.Console.WriteLine("\n-----------------------------------------------------");
            System.Console.WriteLine("-----------------------------------------------------");
            System.Console.WriteLine("   > > >> >>> ELEVATOR " + this.name + bestElevator.id + " WAS CALLED <<< << < <");

            return bestElevator;
        }


        //----------------- Entry method -----------------//
        /* ******* ENTRY METHOD ******* */
        /* ******* REQUEST FOR AN ELEVATOR BY PRESSING THE UP OU DOWN BUTTON OUTSIDE THE ELEVATOR ******* */
        public void requestElevator(int requestedFloor, Direction direction)
        { // User goes to the specific column and press a button outside the elevator requesting for an elevator
            if (direction == Direction.UP)
            {
                //find the UP button by ID
                // Optional<Button> currentButton = this.buttonsUpList.stream().filter(door => door.id == requestedFloor).findFirst();
                Button currentButton = this.buttonsUpList.FirstOrDefault(door => door.id == requestedFloor);
                if (currentButton != null)
                {
                    currentButton.status = ButtonStatus.ON;
                }
            }
            else
            {
                //find the DOWN button by ID
                Button currentButton = this.buttonsDownList.FirstOrDefault(door => door.id == requestedFloor);
                if (currentButton != null)
                {
                    currentButton.status = ButtonStatus.ON;
                }
            }
            //        System.Console.WriteLine(">> Someone request an elevator from floor <" + requestedFloor + "> and direction <" + direction + "> <<");
            Elevator bestElevator = this.findElevator(requestedFloor, direction);
            if (bestElevator.floor != requestedFloor)
            {
                bestElevator.addFloorToFloorList(requestedFloor);
                bestElevator.moveElevator(requestedFloor);
            }
        }

    }


    //------------------------------------------- ELEVATOR CLASS ----------------------------------------------------------------------
    //---------------------------------------------------------------------------------------------------------------------------------
    class Elevator
    {
        public int id;
        public int numberServedFloors;
        public int floor;
        public ElevatorStatus status;
        public SensorStatus weightSensorStatus;
        public SensorStatus obstructionSensorStatus;
        public Column column;
        public Door elevatorDoor;
        public Display elevatorDisplay;
        public List<Door> floorDoorsList;
        public List<Display> floorDisplaysList;
        public List<Button> floorButtonsList;
        public List<int> floorList;

        //----------------- Constructor and its attributes -----------------//
        public Elevator(int elevatorId, int elevatorNumberServedFloors, int elevatorFloor, ElevatorStatus elevatorStatus, SensorStatus weightStatus, SensorStatus obstructionStatus, Column elevatorColumn)
        {
            id = elevatorId;
            numberServedFloors = elevatorNumberServedFloors;
            floor = elevatorFloor;
            status = elevatorStatus;
            weightSensorStatus = weightStatus;
            obstructionSensorStatus = obstructionStatus;
            column = elevatorColumn;
            elevatorDoor = new Door(0, DoorStatus.CLOSED, 0);
            elevatorDisplay = new Display(0, DisplayStatus.ON, 0);
            floorDoorsList = new List<Door>();
            floorDisplaysList = new List<Display>();
            floorButtonsList = new List<Button>();
            floorList = new List<int>();

            // this.createFloorDoorsList();
            // this.createDisplaysList();
            // this.createFloorButtonsList();
        }

        //----------------- Method toString -----------------//
        /* ******* GET A STRING REPRESENTATION OF ELEVATOR OBJECT ******* */
        public override string ToString()
        {
            return "elevator" + column.name + this.id + " | Floor: " + this.floor + " | Status: " + this.status;
        }
        //----------------- Methods to create a list -----------------//
        //----------------- Methods for logic -----------------//
        //----------------- Entry method -----------------//
    }


    //------------------------------------------- DOOR CLASS --------------------------------------------------------------------------
    //---------------------------------------------------------------------------------------------------------------------------------
    class Door
    {
        public int id;
        public DoorStatus status;
        public int floor;

        public Door(int doorId, DoorStatus doorStatus, int doorFloor)
        {
            id = doorId;
            status = doorStatus;
            floor = doorFloor;
        }
    }


    //------------------------------------------- BUTTON CLASS ------------------------------------------------------------------------
    //---------------------------------------------------------------------------------------------------------------------------------
    class Button
    {
        public int id;
        public ButtonStatus status;
        public int floor;

        public Button(int buttonId, ButtonStatus buttonStatus, int buttonFloor)
        {
            id = buttonId;
            status = buttonStatus;
            floor = buttonFloor;
        }
    }


    //------------------------------------------- DISPLAY CLASS -----------------------------------------------------------------------
    //---------------------------------------------------------------------------------------------------------------------------------
    class Display
    {
        public int id;
        public DisplayStatus status;
        public int floor;

        public Display(int displayId, DisplayStatus displayStatus, int displayFloor)
        {
            id = displayId;
            status = displayStatus;
            floor = displayFloor;
        }
    }


    //------------------------------------------- ENUMS -------------------------------------------------------------------------------
    //---------------------------------------------------------------------------------------------------------------------------------
    /* ******* BATTERY STATUS ******* */
    enum BatteryStatus
    {
        ACTIVE,
        INACTIVE
    }

    /* ******* COLUMN STATUS ******* */
    enum ColumnStatus
    {
        ACTIVE,
        INACTIVE
    }

    /* ******* ELEVATOR STATUS ******* */
    enum ElevatorStatus
    {
        IDLE,
        UP,
        DOWN
    }

    /* ******* BUTTONS STATUS ******* */
    enum ButtonStatus
    {
        ON,
        OFF
    }

    /* ******* SENSORS STATUS ******* */
    enum SensorStatus
    {
        ON,
        OFF
    }

    /* ******* DOORS STATUS ******* */
    enum DoorStatus
    {
        OPENED,
        CLOSED
    }

    /* ******* DISPLAY STATUS ******* */
    enum DisplayStatus
    {
        ON,
        OFF
    }

    /* ******* REQUESTED DIRECTION ******* */
    enum Direction
    {
        UP,
        DOWN
    }


    //------------------------------------------- TESTING PROGRAM - SCENARIOS ---------------------------------------------------------
    //---------------------------------------------------------------------------------------------------------------------------------
    class Program
    {
        /* ******* CREATE SCENARIO 1 ******* */
        static void scenario1()
        {
            System.Console.WriteLine("\n****************************** SCENARIO 1: ******************************");
            Battery batteryScenario1 = new Battery(1, 4, 66, 6, 5, BatteryStatus.ACTIVE);
            System.Console.WriteLine(batteryScenario1);
            // batteryScenario1.columnsList.forEach(System.out::println); //batteryScenario1.columnsList.forEach(column -> System.out.println(column));
            //     System.out.println();
            //     //--------- ElevatorB1 ---------
            //     batteryScenario1.columnsList.get(1).elevatorsList.get(0).floor = 20;
            //     batteryScenario1.columnsList.get(1).elevatorsList.get(0).status = ElevatorStatus.DOWN;
            //     batteryScenario1.columnsList.get(1).elevatorsList.get(0).addFloorToFloorList(5);

            //     //--------- ElevatorB2 ---------
            //     batteryScenario1.columnsList.get(1).elevatorsList.get(1).floor = 3;
            //     batteryScenario1.columnsList.get(1).elevatorsList.get(1).status = ElevatorStatus.UP;
            //     batteryScenario1.columnsList.get(1).elevatorsList.get(1).addFloorToFloorList(15);

            //     //--------- ElevatorB3 ---------
            //     batteryScenario1.columnsList.get(1).elevatorsList.get(2).floor = 13;
            //     batteryScenario1.columnsList.get(1).elevatorsList.get(2).status = ElevatorStatus.DOWN;
            //     batteryScenario1.columnsList.get(1).elevatorsList.get(2).addFloorToFloorList(1);

            //     //--------- ElevatorB4 ---------
            //     batteryScenario1.columnsList.get(1).elevatorsList.get(3).floor = 15;
            //     batteryScenario1.columnsList.get(1).elevatorsList.get(3).status = ElevatorStatus.DOWN;
            //     batteryScenario1.columnsList.get(1).elevatorsList.get(3).addFloorToFloorList(2);

            //     //--------- ElevatorB5 ---------
            //     batteryScenario1.columnsList.get(1).elevatorsList.get(4).floor = 6;
            //     batteryScenario1.columnsList.get(1).elevatorsList.get(4).status = ElevatorStatus.DOWN;
            //     batteryScenario1.columnsList.get(1).elevatorsList.get(4).addFloorToFloorList(1);

            //     batteryScenario1.columnsList.get(1).elevatorsList.forEach(System.out::println);
            //     System.out.println();
            //     System.out.println("Person 1: (elevator B5 is expected)"); //elevator expected
            //     System.out.println(">> User request an elevator from floor <1> and direction <UP> <<");
            //     System.out.println(">> User request to go to floor <20>");
            //     batteryScenario1.columnsList.get(1).requestElevator(1, Direction.UP); //parameters (requestedFloor, buttonDirection.UP/DOWN)
            //     batteryScenario1.columnsList.get(1).elevatorsList.get(4).requestFloor(20); //parameters (requestedFloor)
            //     System.out.println("=========================================================================");
            //     System.out.println();
        }

        /* ******* CREATE SCENARIO 2 ******* */

        //------------------------------------------- TESTING PROGRAM - CALL SCENARIOS -----------------------------------------------------
        //----------------------------------------------------------------------------------------------------------------------------------
        static void Main(string[] args)
        {
            Console.WriteLine("Hello World! This is the Commercial Controller in C#!!!");

            /* ******* CALL SCENARIOS ******* */
            scenario1();
            // scenario2();
            // scenario3();
            // scenario4();
        }
    }
}
