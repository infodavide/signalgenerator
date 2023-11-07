import { FormGroup } from "@angular/forms";
import { AbstractComponent } from "./abstract-component";
import { Component, HostListener } from "@angular/core";

@Component({
    template: ''
})
export abstract class AbstractFormComponent extends AbstractComponent {
    form: FormGroup;
    formValid: boolean = false;

    constructor() {
        super();
        this.form = new FormGroup({});
    }

    ngOnInit(): void {
        this._subscriptions.push(this.form.valueChanges.subscribe((changed: any) => {
            this.formValid = this.form.valid;
        }));
    }

    @HostListener('window:keydown.enter', ['$event'])
    handleEnterKeyDown(event: KeyboardEvent) {
        this.submit();
    }

    @HostListener('window:keyup.escape', ['$event'])
    handleEscapeKeyUp(event: KeyboardEvent) {
        this.cancel();
    }

    abstract submit(): void;

    abstract cancel(): void;

    getErrorMessage(name: string): string | null {
        if (!name) {
            return null;
        }

        let control = this.form.controls[name];

        if (control && control.invalid && control.errors) {
            if (control.errors['reason']) {
                return control.errors['reason'];
            }

            if (control.errors['message']) {
                return control.errors['message'];
            }

            return 'Not valid';
        }

        return null;
    }
}