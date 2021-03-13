@project_view
Feature: Project View
	Specification of the behavior of the Project View
	
	Background:
		Given The database contains a few projects
		And The Project View is shown
		And A list is shown containing the projects that are stored in the database

	Scenario: Add a new project
		Given The user provides project name in the text field
		When The user clicks on the "New Project" button
		Then The list contains the new project
		
	Scenario: Delete a project
		When The user clicks on the "Delete Project" button
		Then The project is removed from the list
		
	Scenario: Delete a not existing project
		When The user clicks on the "Delete Project" button
		But In the meantime the project has been removed from the database
		Then An error is shown containing the name of the selected project
		And The project is removed from the list
		
	Scenario: View tasks of a project
		When The user clicks on the "View Tasks" button
		Then The Task View is shown
		
	Scenario: View tasks of a not existing project
		When The user clicks on the "View Tasks" button
		But In the meantime the project has been removed from the database
		Then An error is shown containing the name of the selected project
		And The project is removed from the list