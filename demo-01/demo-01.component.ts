import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Location } from '@angular/common';



@Component({
  selector: 'app-demo-01',
  templateUrl: './demo-01.component.html',
  styleUrls: ['./demo-01.component.css']
})

export class Demo01Component implements OnInit 
{
	/***1 */
	name: string;
	
	/***2 */
	fruits: string[];
	selectedFruit: string;
	
	/***3,4 */
	@Input()
	completeName: string; //child 
	fullName: string;
	
	/***5 */
	today: Date = new Date();

	/***6 */
	paraColour: string;
	choice: number;

	
	ngOnInit()
	{
	}

	
	constructor()
	{
		this.fruits = ["Apple", "Banana", "Chiku", "Durian", "Grape", "Kiwi", "Mango", "Orange", "Pear", "Rambutan"];
		
		/***6 */
		this.paraColour = "blue";
		this.choice = Math.floor((Math.random() * 3) + 1);
	}
	
	toUpperCase(): string
	{
		return this.selectedFruit = this.selectedFruit.toUpperCase();
	}
}