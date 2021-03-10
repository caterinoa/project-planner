Feature: Task View
	Specification of the behavior of the Task View
	
	Background:
		Given The database contains a few tasks for a selected project
		And The Task View related to that project is shown
		And A list is shown containing the tasks that are stored in the database
	
	Scenario: Add a new task
		Given The user provides task description in the text field
		When The user clicks on the "New Task" button
		Then The list contains the new task
		
	Scenario: Delete a task
		When The user clicks on the "Delete Task" button of a task
		Then The task is removed from the list
		
	Scenario: Delete a not existing task
		When The user clicks on the "Delete Task" button of a task
		But In the meantime the task has been removed from the database
		Then An error is shown containing the name of the selected task
		And The task is removed from the list
		
	Scenario: Edit a task
		When The user clicks on the "Edit Task" button of a task
		Then The Edit Task View is shown
		
	Scenario: Edit a task
		When The user clicks on the "Edit Task" button of a task
		But In the meantime the task has been removed from the database
		Then An error is shown containing the name of the selected task
		And The task is removed from the list