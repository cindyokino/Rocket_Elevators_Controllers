""" 
*************************************************
@Author		Cindy Okino
@Website		https://github.com/cindyokino
@Last Update	October 9, 2020


SUMMARY:
0- BATTERY 
1- COLUMN 
2- ELEVATOR 
3- DOOR 
4- BUTTON 
5- DISPLAY 
6- CONSTANTS
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

# ------------------------------------------- BATTERY -----------------------------------------------------------------------------
# ---------------------------------------------------------------------------------------------------------------------------------
defmodule Battery do
   defstruct id: 0, numberOfColumns: 0, minBuildingFloor: 0, maxBuildingFloor: 0, numberOfFloors: 0, numberOfBasements: 0, totalNumberOfFloors: 0, numberOfElevatorsPerColumn: 0, numberOfFloorsPerColumn: 0, status: 0, columnsList: []  
end

# defmodule newBattery do
#    defstruct id: 0, numberOfColumns: 0, minBuildingFloor: 0, maxBuildingFloor: 0, numberOfFloors: 0, numberOfBasements: 0, totalNumberOfFloors: 0, numberOfElevatorsPerColumn: 0, numberOfFloorsPerColumn: 0, status: 0, columnsList: []  
# end


# ------------------------------------------- COLUMN -----------------------------------------------------------------------------
# ---------------------------------------------------------------------------------------------------------------------------------
defmodule Column do
   defstruct id: 0, elevatorsList: []
end


# ------------------------------------------- ELEVATOR -----------------------------------------------------------------------------
# ---------------------------------------------------------------------------------------------------------------------------------
defmodule Elevator do
   defstruct id: 0, numberServedFloors: 0
end

# ------------------------------------------- CONSTANTS -----------------------------------------------------------------------------
# ---------------------------------------------------------------------------------------------------------------------------------
defmodule Constants do

   # ******* BATTERY STATUS ******* 
   def batteryStatus, do: %{
      batteryActive: "Active",
      batteryInactive: "Inactive"     
   }
   
   # ******* COLUMN STATUS ******* 
   def columnStatus, do: %{
      batteryActive: "Active",
      batteryInactive: "Inactive"     
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



defmodule CommercialController do 
   IO.puts "Hello world"
   column = %Column{id: 10, elevatorsList: []} 

   IO.puts column.id
   IO.puts column.elevatorsList
   IO.puts Constants.batteryStatus.batteryActive
end


