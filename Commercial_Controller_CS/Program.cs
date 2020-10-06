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

namespace Commercial_Controller_CS
{
    //------------------------------------------- BATTERY CLASS -----------------------------------------------------------------------
    //---------------------------------------------------------------------------------------------------------------------------------
    class Battery
    {

        //----------------- Constructor and its attributes -----------------//
        //----------------- Method toString -----------------//
        //----------------- Methods to create a list -----------------//
        //----------------- Methods for logic -----------------//
        
    }


    //------------------------------------------- COLUMN CLASS ------------------------------------------------------------------------
    //---------------------------------------------------------------------------------------------------------------------------------
    class Column 
    {

        //----------------- Constructor and its attributes -----------------//
        //----------------- Method toString -----------------//
        //----------------- Methods to create a list -----------------//
        //----------------- Methods for logic -----------------//
        //----------------- Entry method -----------------//
    }


    //------------------------------------------- ELEVATOR CLASS ----------------------------------------------------------------------
    //---------------------------------------------------------------------------------------------------------------------------------
    class Elevator 
    {
        //----------------- Constructor and its attributes -----------------//
        //----------------- Method toString -----------------//
        //----------------- Methods to create a list -----------------//
        //----------------- Methods for logic -----------------//
        //----------------- Entry method -----------------//
    }


    //------------------------------------------- DOOR CLASS --------------------------------------------------------------------------
    //---------------------------------------------------------------------------------------------------------------------------------
    class Door 
    {

    }


    //------------------------------------------- BUTTON CLASS ------------------------------------------------------------------------
    //---------------------------------------------------------------------------------------------------------------------------------
    class Button 
    {

    }


    //------------------------------------------- DISPLAY CLASS -----------------------------------------------------------------------
    //---------------------------------------------------------------------------------------------------------------------------------
    class Display 
    {

    }


    //------------------------------------------- ENUMS -------------------------------------------------------------------------------
    //---------------------------------------------------------------------------------------------------------------------------------
    /* ******* COLUMN STATUS ******* */
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
