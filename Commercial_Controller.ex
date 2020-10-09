""" 
*************************************************
@Author		Cindy Okino
@Website		https://github.com/cindyokino
@Last Update	October 9, 2020


SUMMARY:
0- BATTERY MODULE
1- COLUMN MODULE
2- ELEVATOR MODULE
3- DOOR MODULE
4- BUTTON MODULE
5- DISPLAY MODULE
6- CONSTANTS MODULE
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

**************************************************** 
"""

# ------------------------------------------- CONSTANTS MODULE -----------------------------------------------------------------------------
# ---------------------------------------------------------------------------------------------------------------------------------
defmodule Constants do

   # ******* BATTERY STATUS ******* 
   def batteryStatus, do: %{
      batteryActive: "Active",
      batteryInactive: "Inactive"     
   }
   
   # ******* COLUMN STATUS ******* 
   def columnStatus, do: %{
      columnActive: "Active",
      columnInactive: "Inactive"     
   }

   # ******* ELEVATOR STATUS ******* 
   def elevatorStatus, do: %{
      elevatorIdle: "Idle",
      elevatorUp: "Up",
      elevatorDown: "Down"     
   }

   # ******* BUTTON STATUS ******* 
   def buttonStatus, do: %{
      buttonOn: "On",
      buttonOff: "Off"     
   }

   # ******* SENSOR STATUS ******* 
   def sensorStatus, do: %{
      sensorOn: "On",
      sensorOff: "Off"     
   }

   # ******* DOORS STATUS ******* 
   def doorStatus, do: %{
      doorOpened: "Opened",
      doorClosed: "Closed"    
   }

   # ******* DISPLAY STATUS ******* 
   def displayStatus, do: %{
      displayOn: "On",
      displayOff: "Off"    
   }

   # ******* REQUESTED DIRECTION ******* 
   def columnStatus, do: %{
      directionUp: "Up",
      directionDown: "Down"   
   }

end


# ------------------------------------------- BATTERY MODULE-----------------------------------------------------------------------------
# ---------------------------------------------------------------------------------------------------------------------------------
defmodule Battery do
   defstruct id: 0, numberOfColumns: 0, minBuildingFloor: 0, maxBuildingFloor: 0, numberOfFloors: 0, numberOfBasements: 0, totalNumberOfFloors: 0, numberOfElevatorsPerColumn: 0, numberOfFloorsPerColumn: 0, status: Constants.batteryStatus, columnsList: []  
end


# ------------------------------------------- COLUMN MODULE-----------------------------------------------------------------------------
# ---------------------------------------------------------------------------------------------------------------------------------
defmodule Column do
   defstruct id: 0, name: "", status: Constants.columnStatus.columnActive, numberOfElevatorsPerColumn: 0, minFloor: 0, maxFloor: 0, numberServedFloors: 0, numberOfBasements: 0, battery: nil, elevatorsList: [], buttonsUpList: [], buttonsDownList: [] 
end

# ----------------- Methods to create a list -----------------
# ******* CREATE A LIST OF ELEVATORS FOR THE COLUMN *******
# ******* CREATE A LIST WITH UP BUTTONS FROM THE FIRST FLOOR TO THE LAST LAST BUT ONE FLOOR *******
# ******* CREATE A LIST WITH DOWN BUTTONS FROM THE SECOND FLOOR TO THE LAST FLOOR *******

# ----------------- Methods for logic -----------------    
# ******* LOGIC TO FIND THE BEST ELEVATOR WITH A PRIORITIZATION LOGIC *******


# ------------------------------------------- ELEVATOR MODULE -----------------------------------------------------------------------------
# ---------------------------------------------------------------------------------------------------------------------------------
defmodule Elevator do
   defstruct id: 0, numberServedFloors: 0, floor: 0, status: Constants.elevatorStatus, weightSensorStatus: Constants.sensorStatus, obstructionSensorStatus: Constants.sensorStatus, column: nil, elevatorDoor: Door, elevatorDisplay: Display, floorDoorsList: [], floorDisplaysList: [], floorButtonsList: [], floorList: []

   # ----------------- Functions to create a list -----------------
   # ******* CREATE A LIST WITH A DOOR OF EACH FLOOR *******
   # ******* CREATE A LIST WITH A DISPLAY OF EACH FLOOR *******
   # ******* CREATE A LIST WITH A BUTTON OF EACH FLOOR *******

   # ----------------- Functions for logic -----------------
   # ******* LOGIC TO MOVE ELEVATOR *******
   def moveElevator(requestedFloor, elevator) do
      Enum.each elevator.floorList, fn floor -> 
         if elevator.status == Constants.elevatorStatus.elevatorIdle do
            if elevator.floor < requestedFloor do
               elevator.status = Constants.elevatorStatus.elevatorUp
            else
            if elevator.floor >  requestedFloor do
               elevator.status = Constants.elevatorStatus.elevatorDown
            else
               openDoors(e)
               deleteFloorFromList(requestedFloor, elevator)
               manageButtonStatusOff(requestedFloor, elevator)
            end
            end
         end
         if elevator.status == Constants.elevatorStatus.elevatorUp do
            elevator = moveUp(elevator)
         else
            if elevator.status == Constants.elevatorStatus.elevatorDown do
               elevator = moveDown(elevator)
            end
         end
      end
   end

   # ******* LOGIC TO MOVE UP *******
   # ******* LOGIC TO MOVE DOWN *******
   # ******* LOGIC TO FIND BUTTONS BY ID AND SET BUTTON STATUS OFF *******
   # ******* LOGIC TO UPDATE DISPLAYS OF ELEVATOR AND SHOW FLOOR *******
   # ******* LOGIC TO OPEN DOORS *******
   # ******* LOGIC TO CLOSE DOORS *******
   # ******* LOGIC FOR WEIGHT SENSOR *******
   # ******* LOGIC FOR OBSTRUCTION SENSOR *******
   # ******* LOGIC TO ADD A FLOOR TO THE FLOOR LIST *******
   # ******* LOGIC TO DELETE ITEM FROM FLOORS LIST *******

   # ----------------- Entry method -----------------
   # ******* REQUEST FOR A FLOOR BY PRESSING THE FLOOR BUTTON INSIDE THE ELEVATOR *******



end


# ------------------------------------------- DOOR MODULE -----------------------------------------------------------------------------
# ---------------------------------------------------------------------------------------------------------------------------------
defmodule Door do
   defstruct id: 0, status: 0, floor: 0
end





defmodule CommercialController do 
   IO.puts "Hello world"
   column = %Column{id: 10, elevatorsList: []} 

   IO.puts column.id
   IO.puts column.elevatorsList
   IO.puts Constants.batteryStatus.batteryActive
end


