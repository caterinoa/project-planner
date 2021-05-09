@edit_task_view
Feature: Edit Task View
	Specification of the behavior of the Edit Task View
	
	Background:
		Given The database contains a few tasks for a selected project
		And The Edit Task View is shown
		
	@under_test
	Scenario: Data submission
		Given The user provides task data in the fields
		When The user clicks on the "Submit" button
		Then The Task View is shown containing the updated task