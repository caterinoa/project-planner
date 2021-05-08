@edit_task_view
Feature: Edit Task View
	Specification of the behavior of the Edit Task View
	
	Background:
		Given The Edit Task View is shown
		And The id of the task is shown
	
	Scenario: Data submission
		Given The user provides task data in the fields
		When The user clicks on the "Submit" button
		Then The Task View is shown
		And The updated task is shown in the list