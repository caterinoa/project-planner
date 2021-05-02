@project_view
Feature: Project View
	Specification of the behavior of the Project View
	
	Background:
		Given The database contains a few projects
		And The Project View is shown
		And A list is shown containing the projects that are stored in the database

	@under_test
	Scenario: Add a new project
		Given The user provides project name in the text field
		When The user clicks on the "New Project" button
		Then The list contains the new project
	
	@under_test		
	Scenario: Delete a project
		When The user clicks on the "Delete Project" button
		Then The project is removed from the list

	@under_test
	Scenario: Delete a not existing project
		Given A project has been removed from the database
		When The user clicks on the "Delete Project" button
		Then An error is shown containing the id of the selected project
	
	@under_test
	Scenario: View tasks of a project
		When The user clicks on the "View Tasks" button
		Then The Task View is shown
	
	@under_test
	Scenario: View tasks of a not existing project
		Given A project has been removed from the database
		When The user clicks on the "View Tasks" button
		Then An error is shown containing the id of the selected project
		
		