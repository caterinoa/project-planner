@task_view
Feature: Task View
	Specification of the behavior of the Task View
	
	Background:
		Given The database contains a few tasks for a selected project
		And The Task View is shown
		And The id of the project is shown
		And A list is shown containing the tasks that are stored in the database for the project 
	
	@under_test
	Scenario: Add a new task
		Given The user provides task description in the text field
		When The user clicks on the "New Task" button
		Then The list contains the new task
		
	Scenario: Delete a task
		When The user clicks on the "Delete Task" button
		Then The task is removed from the list
		
	Scenario: Delete a not existing task
		Given A task has been removed from the database
		When The user clicks on the "Delete Task" button
		Then An error is shown containing the id of the selected task
		
	Scenario: Edit a task
		When The user clicks on the "Edit Task" button
		Then The Edit Task View is shown
		
	Scenario: Edit a not existing task
		Given A task has been removed from the database
		When The user clicks on the "Edit Task" button
		Then An error is shown containing the id of the selected task

		