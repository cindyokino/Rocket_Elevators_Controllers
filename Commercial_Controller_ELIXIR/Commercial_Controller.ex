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
   def requestedDirection, do: %{
      directionUp: "Up",
      directionDown: "Down"
   }

end


# ------------------------------------------- BATTERY MODULE-----------------------------------------------------------------------------
# ---------------------------------------------------------------------------------------------------------------------------------
defmodule Battery do
   defstruct id: 0, numberOfColumns: 0, totalNumberOfFloors: 0, numberOfBasements: 0, numberOfElevatorsPerColumn: 0, status: Constants.batteryStatus, columnsList: []

   def printBattery(battery) do
      IO.puts battery.id
      IO.puts battery.numberOfColumns
      IO.puts battery.totalNumberOfFloors
      IO.puts battery.numberOfBasements
      IO.puts battery.numberOfElevatorsPerColumn
      IO.puts battery.status
      Enum.each(battery.columnsList, fn (col) ->
         Column.printColumn(col) end)
   end
end


# ------------------------------------------- COLUMN MODULE-----------------------------------------------------------------------------
# ---------------------------------------------------------------------------------------------------------------------------------
defmodule Column do
   defstruct id: 0, name: "", status: Constants.columnStatus.columnActive, numberOfElevatorsPerColumn: 0, minFloor: 0, maxFloor: 0, numberServedFloors: 0, numberOfBasements: 0, battery: nil, elevatorsList: [], buttonsUpList: [], buttonsDownList: []

   def printColumn(column) do
      IO.puts column.id
      IO.puts column.name
      IO.puts column.status
      IO.puts column.numberOfElevatorsPerColumn
      IO.puts column.minFloor
      IO.puts column.maxFloor
      IO.puts column.numberServedFloors
      IO.puts column.numberOfBasements
      Enum.each(column.elevatorsList, fn (elevator) ->
         Elevator.printElevator(elevator) end)
   end
end

# ----------------- Methods to create a list -----------------


# ******* CREATE A LIST WITH UP BUTTONS FROM THE FIRST FLOOR TO THE LAST LAST BUT ONE FLOOR *******
# ******* CREATE A LIST WITH DOWN BUTTONS FROM THE SECOND FLOOR TO THE LAST FLOOR *******

# ----------------- Methods for logic -----------------
# ******* LOGIC TO FIND THE BEST ELEVATOR WITH A PRIORITIZATION LOGIC *******
# public Elevator findElevator(int currentFloor, Direction direction) {
#    Elevator bestElevator;
#    List<Elevator> activeElevatorList = new ArrayList<>();
#    List<Elevator> idleElevatorList = new ArrayList<>();
#    List<Elevator> sameDirectionElevatorList = new ArrayList<>();
#    this.elevatorsList.forEach(elevator -> {
#       if (elevator.status != ElevatorStatus.IDLE) {
#             //Verify if the request is on the elevators way, otherwise the elevator will just continue its way ignoring this call
#             if (elevator.status == ElevatorStatus.UP && elevator.floor <= currentFloor || elevator.status == ElevatorStatus.DOWN && elevator.floor >= currentFloor) {
#                activeElevatorList.add(elevator);
#             }
#       } else {
#             idleElevatorList.add(elevator);
#       }
#    });

#    if (activeElevatorList.size() > 0) { //Create new list for elevators with same direction that the request
#       sameDirectionElevatorList = activeElevatorList.stream().filter(elevator -> elevator.status.name().equals(direction.name())).collect(Collectors.toList());
#    }

#    if (sameDirectionElevatorList.size() > 0) {
#       bestElevator = this.findNearestElevator(currentFloor, sameDirectionElevatorList); // 1- Try to use an elevator that is moving and has the same direction
#    } else if (idleElevatorList.size() > 0){
#       bestElevator = this.findNearestElevator(currentFloor, idleElevatorList); // 2- Try to use an elevator that is IDLE
#    } else {
#       bestElevator = this.findNearestElevator(currentFloor, activeElevatorList); // 3- As the last option, uses an elevator that is moving at the contrary direction
#    }

#    return bestElevator;
# }

# ******* LOGIC TO FIND THE NEAREST ELEVATOR *******
# ******* LOGIC TO TURN ON THE BUTTONS FOR THE ASKED DIRECTION *******

# ----------------- Entry method -----------------
# ******* ENTRY METHOD *******
# ******* REQUEST FOR AN ELEVATOR BY PRESSING THE UP OU DOWN BUTTON OUTSIDE THE ELEVATOR *******



# ------------------------------------------- ELEVATOR MODULE -----------------------------------------------------------------------------
# ---------------------------------------------------------------------------------------------------------------------------------
defmodule Elevator do
   defstruct id: 0, numberServedFloors: 0, floor: 0, status: Constants.elevatorStatus, weightSensorStatus: Constants.sensorStatus, obstructionSensorStatus: Constants.sensorStatus, column: nil, elevatorDoor: Door, elevatorDisplay: Display, floorDoorsList: [], floorDisplaysList: [], floorButtonsList: [], floorList: []

   def printElevator(elevator) do
      IO.puts elevator.id
      IO.puts elevator.numberServedFloors
      IO.puts elevator.floor
      IO.puts elevator.status
      IO.puts elevator.weightSensorStatus
      IO.puts elevator.elevatorDoor
      IO.puts elevator.elevatorDisplay
   end

   # ----------------- Functions to create a list -----------------
   # ******* CREATE A LIST WITH A DOOR OF EACH FLOOR *******
   # ******* CREATE A LIST WITH A DISPLAY OF EACH FLOOR *******
   # ******* CREATE A LIST WITH A BUTTON OF EACH FLOOR *******

   # ----------------- Functions for logic -----------------
   # ******* LOGIC TO MOVE ELEVATOR *******
   def moveElevator(requestedFloor, elevator) do

      if elevator.floor < requestedFloor do
         # elevator.status = Constants.elevatorStatus.elevatorUp
         moveUp(requestedFloor, elevator)
      else
         if elevator.floor >  requestedFloor do
            # elevator.status = Constants.elevatorStatus.elevatorDown
            Elevator.moveDown(requestedFloor, elevator)
         else
            Elevator.openDoors(requestedFloor, elevator)
         end
      end
   end

   # ******* LOGIC TO MOVE UP *******
   def moveUp(requestedFloor, elevator) do
      # IO.puts "\t\t Moving elevator UP from #{requestedFloor} to #{requestedFloor + 1}"
      IO.puts "\t Moving elevator UP to floor #{requestedFloor}"
   end
   # ******* LOGIC TO MOVE DOWN *******
   def moveDown(requestedFloor, elevator) do
      # IO.puts "\t\t Moving elevator DOWN from #{requestedFloor} to #{requestedFloor - 1}"
      IO.puts "\t Moving elevator DOWN to floor #{requestedFloor}"
   end

   # ******* LOGIC TO FIND BUTTONS BY ID AND SET BUTTON STATUS OFF *******
   # ******* LOGIC TO UPDATE DISPLAYS OF ELEVATOR AND SHOW FLOOR *******

   # ******* LOGIC TO OPEN DOORS *******
   def openDoors(requestedFloor, elevator) do
      IO.puts "Elevator doors are opening...}"
      IO.puts "Elevator doors are opened}"
      Elevator.closeDoors(requestedFloor, elevator)
   end

   # ******* LOGIC TO CLOSE DOORS *******
   def closeDoors(requestedFloor, elevator) do
      IO.puts "Elevator doors are closing...}"
      IO.puts "Elevator doors are closed}"
   end



# ******* CREATE A LIST OF ELEVATORS FOR THE COLUMN *******
   def createElevatorsList(numberOfElevatorsPerColumn) do
      Enum.map(1..numberOfElevatorsPerColumn, fn (i) ->
         IO.puts i
         %Elevator{id: i, numberServedFloors: 0, floor: 1, status: Constants.elevatorStatus.elevatorIdle, weightSensorStatus: Constants.sensorStatus.sensorOff, obstructionSensorStatus: Constants.sensorStatus.sensorOff, column: nil, elevatorDoor: Door, elevatorDisplay: Display, floorDoorsList: [], floorDisplaysList: [], floorButtonsList: [], floorList: []}
      end)
   end


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





# ------------------------------------------- TESTING PROGRAM - CALL SCENARIOS -----------------------------------------------------
# ----------------------------------------------------------------------------------------------------------------------------------
defmodule CommercialController do
   # ******* CALL SCENARIOS *******
# ------------------------------------------- TESTING PROGRAM - SCENARIOS ---------------------------------------------------------
# ---------------------------------------------------------------------------------------------------------------------------------
# ******* CREATE SCENARIO 1 *******
# def scenario1(requestedFloor, elevator) do
#    IO.puts "Welcome to the Elevator !!!"
#    battery1 = %Battery{id: 1, numberOfColumns: 4, totalNumberOfFloors: 66, numberOfBasements: 6, numberOfElevatorsPerColumn: 5, status: Constants.batteryStatus.batteryActive, columnsList: [] }
#    IO.puts "\t Battery created #{battery1.id}"
#    column = %Column{id: 10, elevatorsList: []}
#    Elevator.moveElevator(requestedFloor, elevator)
# end

#   Elevator.printElevator(elevator1)
   elevatorsList = Elevator.createElevatorsList(5)

   IO.inspect elevatorsList


   # el1 = Enum.fetch(elist, 0)


end
