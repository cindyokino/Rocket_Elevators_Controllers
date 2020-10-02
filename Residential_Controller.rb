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
$numberOfColumns
$numberOfFloors    
$numberOfElevators
$waitingTime         # How many time the door remains opened in SECONDS
$maxWeight           # Maximum weight an elevator can carry in KG


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


end



# ------------------------------------------- TESTING PROGRAM ------------------------------------------------------------------------
# ---------------------------------------------------------------------------------------------------------------------------------
waitingTime = 1 #How many time the door remains opened in SECONDS - I'm using 1 second so the test will run faster
maxWeight = 500 #Maximum weight an elevator can carry in KG

''' ******* CREATE SCENARIO 1 ******* '''
def scenario1(): 
    print()
    print("****************************** SCENARIO 1: ******************************")
    columnScenario1 = Column.new(1, ColumnStatus.ACTIVE, 10, 2) #parameters (id, ColumnStatus.ACTIVE/INACTIVE, numberOfFloors, numberOfElevators)
    columnScenario1.display()  
    columnScenario1.elevatorsList[0].floor = 2 #floor where the elevator 1 is
    columnScenario1.elevatorsList[1].floor = 6 #floor where the elevator 2 is
    
    print()
    print("Person 1: (elevator 1 is expected)")
    columnScenario1.requestElevator(3, ButtonDirection.UP) #parameters (requestedFloor, buttonDirection.UP/DOWN)
    columnScenario1.elevatorsList[0].requestFloor(7, columnScenario1) #parameters (requestedFloor, requestedColumn)
    print("==================================")