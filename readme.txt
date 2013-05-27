=======================
======IOU Tracker======
=======================
This is an IOU Tracker that makes use of GWT, Spring, and MyBatis.
This webapp was created by Jeffrey Chien.
=======================
Todo
=======================
1. Keep the table sorted by date even as you move entries from the open table to the closed table.
2. Use XML/UiBinder for layout.
=======================
Features
=======================
1. List of IOUs (i.e. covering one person/ multiple people 
   for a bill or buying someone food)
2. IOU contain either one person or multiple people and amount due for 
   each person
3. The person issuing the IOU signs off the person as they pay 
4. History of paid off IOUs and IOUs that are not paid off by 
   everyone in separate tables and sorted by chronological order 
  
=======================
Frontend/UI
=======================
1. Signed out
2. Overview
	Open IUO – number of people that have paid the money < number of people that owe money
	Closed IUO – number of people that have paid the money == number of people 
	that owe money
	2 tables – one for open IOUs and one for closed IOUs
		Title
		Date issued
		Total amount paid/total amount due
		Number of people that owe money/number of people that owe money 
		(only in open IOU)
3. IOU details
	1 table 
		List of people that owe(d) money
			Unpaid
				Check checkbox to confirm payment
			Paid
				Uncheck checkbox with popup confirmation
				Date paid
			Both
				See Name
				See Amount Due
		Number of people paid/ number of people that have not paid
		Total amount paid/total amount due
		Finish 
	Edit
		Add people to IOU
		Remove people from IOU with confirmation
		Edit people names and amounts
=======================
Backend
=======================
user
	id SERIAL PRIMARY KEY
	username TEXT
	password TEXT
	name TEXT
entries	
	id SERIAL PRIMARY KEY 
	title TEXT
	date DATE
	issuer TEXT
	aountdue DECIMAL(10,2)
	amountpaid DECIMAL(10,2)
	numberborrowers INT
borrowers
	entryid INT
	name TEXT
	amountdue DECIMAL(10,2)
	haspaid INT

=========================
PostgreSQL Queries
=========================
CREATE DATABASE iou;
\c iou
CREATE TABLE user (id SERIAL PRIMARY KEY, username TEXT, password TEXT, name TEXT);
CREATE TABLE entries(id SERIAL PRIMARY KEY, title TEXT, date DATE, issuer TEXT, amount DECIMAL(10,2),amountpaid DECIMAL(10,2), numberborrowers INT);
CREATE TABLE borrowers (entryid INT, name TEXT, amountdue DECIMAL(10,2), haspaid INT);				 
