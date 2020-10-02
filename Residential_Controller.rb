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
    attr_accessor :id, :columnStatus, :numberOfFloors, :numberOfElevators, :elevatorsList, :buttonsUpList
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
            @elevatorsList.append(Elevator.new(x, @numberOfFloors, 1, ElevatorStatus::IDLE, SensorStatus::OFF, SensorStatus::OFF))
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
            # puts "button down #{@buttonsDownList[x - 2].id} created"
        end
    end


    #  ------------------ Entry method ------------------
    # CREATE A LIST WITH A BUTTON OF EACH FLOOR
    # REQUEST FOR AN ELEVATOR BY PRESSING THE UP OU DOWN BUTTON OUTSIDE THE ELEVATOR
    def requestElevator(requestedFloor, direction)
        if direction == ButtonDirection::UP
            @buttonsUpList[requestedFloor-1].status = ButtonStatus::ON
        else
            @buttonsDownList[requestedFloor-2].status = ButtonStatus::ON
        end

        puts ">> Someone request an elevator from floor <#{requestedFloor}> and direction <#{direction}> <<"
        for x in @elevatorsList do
            # puts "Elevator#{@elevatorsList[x - 1].id} | Floor: #{@elevatorsList[x - 1].floor | Status: #{@elevatorsList[x - 1].status}"
        end

        bestElevator = findElevator(requestedFloor, direction)
        bestElevator.addFloorToFloorList(requestedFloor) 
        bestElevator.moveElevator(requestedFloor, self)
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

        createFloorDoorsList
        createDisplaysList
        createFloorButtonsList
    end

    # CREATE A LIST WITH A DOOR OF EACH FLOOR
    def createFloorDoorsList
        for x in 1..@numberOfFloors do
            @floorDoorsList.append(Door.new(x, DoorStatus::CLOSED, x))
            # puts "elevator#{@id} door floor #{@floorDoorsList[x - 1].id} created"
        end
    end

    # CREATE A LIST WITH A DISPLAY OF EACH FLOOR
    def createDisplaysList
        for x in 1..@numberOfFloors do
            @floorDisplaysList.append(Display.new(x, DisplayStatus::ON, x))
            # puts "elevator#{@id} display floor #{@floorDisplaysList[x - 1].id} created"
        end
    end

    # CREATE A LIST WITH A BUTTON OF EACH FLOOR
    def createFloorButtonsList
        for x in 1..@numberOfFloors do
            @floorButtonsList.append(Button.new(x, ButtonStatus::ON, x))
            # puts "elevator#{@id} button floor #{@floorButtonsList[x - 1].id} created"
        end
    end


    #  ------------------ Methods for logic ------------------
    # LOGIC TO MOVE ELEVATOR
    def moveElevator(requestedFloor, requestedColumn)
        while @floorList.length() != 0
            if @status == ElevatorStatus::IDLE
                if @floor < requestedFloor
                    @status = ElevatorStatus::UP
                elsif @floor == requestedFloor
                    openDoors(waitingTime)
                    deleteFloorFromList(requestedFloor)
                    requestedColumn.buttonsUpList[requestedFloor-1].status = ButtonStatus::OFF
                    requestedColumn.buttonsDownList[requestedFloor-1].status = ButtonStatus::OFF
                    @floorButtonsList[requestedFloor-1].status = ButtonStatus::OFF
                else
                    @status = ElevatorStatus::DOWN
                end
            end

            if @status == ElevatorStatus::UP
                moveUp(requestedColumn)
            else
                moveDown(requestedColumn)
            end

        end
    end

    # LOGIC TO MOVE UP
    def moveUp(requestedColumn)
        tempArray = @floorList.dup
        for x in @floor..(tempArray[len(tempArray) - 1])
            if @floorDoorsList[x].status == DoorStatus::OPENED or @elevatorDoor.status == DoorStatus::OPENED
                puts "   Doors are open, closing doors before move up"
                closeDoors
            end
            
            puts "Moving elevator#{(@id)} <up> from floor #{x} to floor #{x + 1}"
            nextFloor = (x + 1)
            @floor = nextFloor
            updateDisplays(@floor)
            
            if tempArray.include? nextFloor
                openDoors(waitingTime)
                deleteFloorFromList(nextFloor)
                requestedColumn.buttonsUpList[x - 1].status = ButtonStatus::OFF
                floorButtonsList[x].status = ButtonStatus::OFF
            end
        end
            
        if @floorList.length() == 0
            @status = ElevatorStatus::IDLE
            # puts "       Elevator#{@id} is now #{@status.value}"
        else
            @status = ElevatorStatus::DOWN
            puts "       Elevator#{@id} is now going #{@status.value}"
        end
    end

    # LOGIC TO MOVE DOWN
    def moveDown(requestedColumn)
        tempArray = @floorList.dup
        for x in @floor.downto(tempArray[len(tempArray) - 1])
            if @floorDoorsList[x - 1].status == DoorStatus::OPENED or @elevatorDoor.status == DoorStatus::OPENED
                puts "   Doors are open, closing doors before move down"
                closeDoors
            end
            
            puts "Moving elevator#{(@id)} <down> from floor #{x} to floor #{x - 1}"
            nextFloor = (x - 1)
            @floor = nextFloor
            updateDisplays(@floor)
            
            if tempArray.include? nextFloor
                openDoors(waitingTime)
                deleteFloorFromList(nextFloor)
                requestedColumn.buttonsUpList[x - 2].status = ButtonStatus::OFF
                floorButtonsList[x - 1].status = ButtonStatus::OFF
            end
        end
            
        if @floorList.length() == 0
            @status = ElevatorStatus::IDLE
            # puts "       Elevator#{@id} is now #{@status.value}"
        else
            @status = ElevatorStatus::UP
            puts "       Elevator#{@id} is now going #{@status.value}"
        end
    end

    # LOGIC TO UPDATE DISPLAYS OF ELEVATOR AND SHOW FLOOR
    def updateDisplays(elevatorFloor)
        for display in @floorDisplaysList
            display.floor = elevatorFloor
        end
        
        puts "Displays show ##{elevatorFloor}"
    end

    # LOGIC TO OPEN DOORS
    def openDoors(waitingTime)
        puts "       Opening doors..."
        puts "       Elevator#{@id} doors are opened"
        @elevatorDoor.status.OPENED
        @floorDoorsList[@floor-1].status = DoorStatus::OPENED
        sleep(waitingTime)
        closeDoors
    end

    # LOGIC TO CLOSE DOORS
    def closeDoors
        if @weightSensor == SensorStatus::OFF and @obstructionSensor == SensorStatus::OFF
            puts "       Closing doors..."
            puts "       Elevator#{@id} doors are closed"
            @floorDoorsList[@floor-1].status = DoorStatus::CLOSED
        end
    end

    # LOGIC FOR WEIGHT SENSOR
    def checkWeight(maxWeight)
        weight = rand(1..600) #This random simulates the weight from a weight sensor
        while weight > maxWeight
            @weightSensor = SensorStatus::ON
            puts "       ! Elevator capacity reached, waiting until the weight is lower before continue..."
            weight -= 100 #I'm supposing the random number is 600, I'll subtract 100 so it will be less than 500 (the max weight I proposed) for the second time it runs
        end

        @weightSensor = SensorStatus::OFF
        puts "       Elevator capacity is OK"
    end

    # LOGIC FOR OBSTRUCTION SENSOR
    def checkObstruction
        probabilityNotBlocked = 70
        number = rand(1..100) #This random simulates the probability of an obstruction (I supposed 30% of chance something is blocking the door)

        while number > probabilityNotBlocked
            @obstructionSensor = SensorStatus::ON
            puts "       ! Elevator door is blocked by something, waiting until door is free before continue..."
            number -= 30  #I'm supposing the random number is 100, I'll subtract 30 so it will be less than 70 (30% probability), so the second time it runs theres no one blocking the door
        end

        @obstructionSensor = SensorStatus::OFF
        puts "       Elevator door is FREE"
    end

    # LOGIC TO ADD A FLOOR TO THE FLOOR LIST 
    def addFloorToFloorList(floor)
        @floorList.append(floor)
        puts "Elevator#{self.id} - floor #{floor} added to floorList"
    end

    # LOGIC TO DELETE ITEM FROM FLOORS LIST
    def deleteFloorFromList(stopFloor)
        index = @floorList.find_index(stopFloor)
        if index > -1
            @floorList.delete(index)
        end
    end


    #  ------------------ Entry method ------------------
    # CREATE A LIST WITH A BUTTON OF EACH FLOOR
    # REQUEST FOR A FLOOR BY PRESSING THE FLOOR BUTTON INSIDE THE ELEVATOR
    def requestFloor(requestedFloor, requestedColumn)
        puts ""          
        puts ">> Someone inside the elevator#{@id} wants to go to floor <#{requestedFloor}> <<"
        checkWeight(maxWeight)
        checkObstruction()
        addFloorToFloorList(requestedFloor)
        moveElevator(requestedFloor, requestedColumn)
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
    attr_accessor :id, :status, :floor
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
    columnScenario1.elevatorsList[0].floor = 2 #floor where the elevator 1 is
    columnScenario1.elevatorsList[1].floor = 6 #floor where the elevator 2 is
    
    puts ""
    puts "Person 1: (elevator 1 is expected)"
    columnScenario1.requestElevator(3, ButtonDirection::UP) #parameters (requestedFloor, buttonDirection.UP/DOWN)
#     columnScenario1.elevatorsList[0].requestFloor(7, columnScenario1) #parameters (requestedFloor, requestedColumn)
    puts "=================================="
end


''' -------- CALL SCENARIOS -------- '''
scenario1
# scenario2
# scenario3
