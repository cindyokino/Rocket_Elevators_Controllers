=begin 
***************************************************
	@Author			Cindy Okino
	@Website		https://github.com/cindyokino
	@Last Update	October 2, 2020
    
    
SUMMARY:
1- GLOBAL VARIABLES
2- COLUMN CLASS
    2a- Constructor and its attributes
    2b- Methods to create a list: createElevatorsList, createButtonsUpList, createButtonsDownList
    2c- Methods for logic: findElevator, findNearestElevator
    2d- Entry method: requestElevator
3- ELEVATOR CLASS
    3a- Constructor and its attributes
    3b- Methods to create a list: createFloorDoorsList, createDisplaysList, createFloorButtonsList, addFloorToFloorList
    3c- Methods for logic: moveElevator, moveUp, moveDown, updateDisplays, openDoors, closeDoors, checkWeight, checkObstruction, deleteFloorFromList
    3d- Entry method: requestFloor
4- DOOR CLASS
5- BUTTON CLASS
6- DISPLAY CLASS
7- ENUMS
8- TESTING PROGRAM
9- TEST YOUR SCENARIO

CONTROLLED OBJECTS:
Columns: controls a list of N elevators
Elevators: controls doors, buttons, displays

*************************************************** 
=end

# ------------------------------------------- GLOBAL VARIABLES ---------------------------------------------------------------------
# ----------------------------------------------------------------------------------------------------------------------------------
$numberOfColumns = 0
$numberOfFloors = 0 
$numberOfElevators = 0
$waitingTime = 0         # How many time the door remains opened in SECONDS
$maxWeight = 0           # Maximum weight an elevator can carry in KG


# ------------------------------------------- COLUMN CLASS ------------------------------------------------------------------------
# ---------------------------------------------------------------------------------------------------------------------------------
class Column
    #  ------------------ Constructor and its attributes ------------------
    attr_accessor :id, :columnStatus, :numberOfFloors, :numberOfElevators
    def initialize(id, columnStatus, numberOfFloors, numberOfElevators)
        @id = id
        @status = columnStatus
        @numberOfFloors = numberOfFloors
        @numberOfElevators = numberOfElevators
        @elevatorsList = []
        @buttonsUpList = []
        @buttonsDownList = []

        createElevatorsList
        createButtonsUpList
        createButtonsDownList    
    end

    def display
        puts "Created column #{@id}"
        puts "Number of floors: #{@numberOfFloors}"
        puts "Created Number of elevators: #{@numberOfElevators}"
        puts "----------------------------------"
    end

    #  ------------------ Methods to create a list ------------------
    # CREATE A LIST OF ELEVATORS FOR THE COLUMN
    def createElevatorsList
        for x in 1..@numberOfElevators do
            @elevatorsList.append(Elevator.new(x + 1, @numberOfFloors, 1, ElevatorStatus::IDLE, SensorStatus::OFF, SensorStatus::OFF))
            # puts "elevator#{@id} created"
        end
    end

    # CREATE A LIST WITH UP BUTTONS FROM THE FIRST FLOOR TO THE LAST LAST BUT ONE FLOOR
    def createButtonsUpList
        for x in 1..(@numberOfFloors - 1) do
            @buttonsUpList.append(Button.new(x, ButtonStatus::OFF, x))
            # puts "button up #{@buttonsUpList[x - 1].id} created"
        end
    end

    # CREATE A LIST WITH DOWN BUTTONS FROM THE SECOND FLOOR TO THE LAST FLOOR
    def createButtonsDownList
        for x in 2..@numberOfFloors do
            @buttonsDownList.append(Button.new(x, ButtonStatus::OFF, x))
            puts "button down #{@buttonsDownList[x - 2].id} created"
        end
    end

end


# ------------------------------------------- ELEVATOR CLASS ------------------------------------------------------------------------
# ---------------------------------------------------------------------------------------------------------------------------------
class Elevator
    #  ------------------ Constructor and its attributes ------------------
    attr_accessor :id, :numberOfFloors, :floor, :elevatorStatus, :weightSensorStatus, :obstructionSensorStatus
    def initialize(id, numberOfFloors, floor, elevatorStatus, weightSensorStatus, obstructionSensorStatus)
        @id = id
        @numberOfFloors = numberOfFloors
        @floor = floor
        @status = elevatorStatus
        @weightSensor = weightSensorStatus
        @obstructionSensor = obstructionSensorStatus
        @elevatorDoor = Door.new(0, DoorStatus::CLOSED, 0)
        @elevatorDisplay = Display.new(0, DisplayStatus::ON, 0)
        @floorDoorsList = []
        @floorDisplaysList = []
        @floorButtonsList = []
        @floorList = []

        # @createFloorDoorsList
        # @createDisplaysList
        # @createFloorButtonsList
    end

end


# ------------------------------------------- DOOR CLASS ------------------------------------------------------------------------
# ---------------------------------------------------------------------------------------------------------------------------------
class Door
    attr_accessor :id, :columnStatus, :numberOfFloors, :numberOfElevators
    def initialize(id, doorStatus, floor)
        @id = id
        @status = doorStatus
        @floor = floor
    end
end


# ------------------------------------------- BUTTON CLASS ------------------------------------------------------------------------
# ---------------------------------------------------------------------------------------------------------------------------------
class Button
    attr_accessor :id, :columnStatus, :numberOfFloors, :numberOfElevators
    def initialize(id, buttonStatus, floor)
        @id = id
        @status = buttonStatus
        @floor = floor
    end
end


# ------------------------------------------- DISPLAY CLASS ------------------------------------------------------------------------
# ---------------------------------------------------------------------------------------------------------------------------------
class Display
    attr_accessor :id, :columnStatus, :numberOfFloors, :numberOfElevators
    def initialize(id, displayStatus, floor)
        @id = id
        @status = displayStatus
        @floor = floor
    end
end


# ------------------------------------------- ENUMS ------------------------------------------------------------------------
# ---------------------------------------------------------------------------------------------------------------------------------
# --COLUMN STATUS -- 
module ColumnStatus
    ACTIVE = "active"
    INACTIVE = 'inactive'
end

# -- ELEVATOR STATUS --
module ElevatorStatus
    IDLE = 'idle'
    UP = 'up'
    DOWN = 'down'
end

# -- BUTTON DIRECTION --
module ButtonDirection
    UP = 'up'
    DOWN = 'down'
end

# -- BUTTON STATUS --
module ButtonStatus
    ON = 'on'
    OFF = 'off'
end

# -- SENSOR STATUS --
module SensorStatus
    ON = 'on'
    OFF = 'off'
end

# -- DOORS STATUS --
module DoorStatus
    OPENED = 'opened'
    CLOSED = 'closed'
end

# -- DISPLAY STATUS --
module DisplayStatus
    ON = 'on'
    OFF = 'off'
end


# ------------------------------------------- TESTING PROGRAM ------------------------------------------------------------------------
# ---------------------------------------------------------------------------------------------------------------------------------
waitingTime = 1 #How many time the door remains opened in SECONDS - I'm using 1 second so the test will run faster
maxWeight = 500 #Maximum weight an elevator can carry in KG

# ******* CREATE SCENARIO 1 ******* 
def scenario1()
    puts ""
    puts "****************************** SCENARIO 1: ******************************"
    columnScenario1 = Column.new(1, ColumnStatus::ACTIVE, 10, 2) #parameters (id, ColumnStatus.ACTIVE/INACTIVE, numberOfFloors, numberOfElevators)
    columnScenario1.display()  
#     columnScenario1.elevatorsList[0].floor = 2 #floor where the elevator 1 is
#     columnScenario1.elevatorsList[1].floor = 6 #floor where the elevator 2 is
    
    puts ""
    puts "Person 1: (elevator 1 is expected)"
#     columnScenario1.requestElevator(3, ButtonDirection.UP) #parameters (requestedFloor, buttonDirection.UP/DOWN)
#     columnScenario1.elevatorsList[0].requestFloor(7, columnScenario1) #parameters (requestedFloor, requestedColumn)
    puts "=================================="
end

''' -------- CALL SCENARIOS -------- '''
scenario1
