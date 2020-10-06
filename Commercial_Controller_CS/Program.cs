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
            // createColumnsList();
            // setColumnValues();
            // createListsInsideColumns();
        }
        

        //----------------- Constructor and its attributes -----------------//
        //----------------- Method toString -----------------//
        //----------------- Methods to create a list -----------------//
        //----------------- Methods for logic -----------------//
        
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
        //----------------- Methods to create a list -----------------//
        //----------------- Methods for logic -----------------//
        //----------------- Entry method -----------------//
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

        //------------------------------------------- TESTING PROGRAM - CALL SCENARIOS -----------------------------------------------------
        //----------------------------------------------------------------------------------------------------------------------------------
        static void Main(string[] args)
        {
            Console.WriteLine("Hello World! This is the Commercial Controller in C#!!!");
        }
    }
}
