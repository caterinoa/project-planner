Feature: Edit Task View
	Specification of the behavior of the Edit Task View
	
	Background:
		Given The Edit Task View related to a task is shown
	
	Scenario: Data submission
		Given The user provides task data in the fields
		When The user clicks on the "Submit" button
		Then The Task View is shown
		
	Scenario: Invalid data submission
		Given The user provides task data in the fields
		But The provided due date is invalid
		When The user clicks on the "Submit" button
		Then An error is shown containing the invalid date