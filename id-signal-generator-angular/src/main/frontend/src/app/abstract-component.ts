import { Component, OnDestroy } from "@angular/core";
import { Subscription } from "rxjs";

@Component({
  template: ''
})
export abstract class AbstractComponent implements OnDestroy {
    protected _subscriptions: Subscription[] = [];
    
    constructor() {    
    }
    
    ngOnDestroy(): void {
      this._subscriptions.forEach(s => s.unsubscribe());
    }
  }