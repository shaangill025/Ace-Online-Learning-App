import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { SmartCpdButtonDemoModule } from './buttons/button/buttondemo.module';
import { SmartCpdSplitbuttonDemoModule } from './buttons/splitbutton/splitbuttondemo.module';
import { SmartCpdConfirmDialogDemoModule } from './overlay/confirmdialog/confirmdialogdemo.module';
import { SmartCpdLightboxDemoModule } from './overlay/lightbox/lightboxdemo.module';
import { SmartCpdTooltipDemoModule } from './overlay/tooltip/tooltipdemo.module';
import { SmartCpdOverlayPanelDemoModule } from './overlay/overlaypanel/overlaypaneldemo.module';
import { SmartCpdSideBarDemoModule } from './overlay/sidebar/sidebardemo.module';
import { SmartCpdKeyFilterDemoModule } from './inputs/keyfilter/keyfilterdemo.module';
import { SmartCpdInputTextDemoModule } from './inputs/inputtext/inputtextdemo.module';
import { SmartCpdInputTextAreaDemoModule } from './inputs/inputtextarea/inputtextareademo.module';
import { SmartCpdInputGroupDemoModule } from './inputs/inputgroup/inputgroupdemo.module';
import { SmartCpdCheckboxDemoModule } from './inputs/checkbox/checkboxdemo.module';
import { SmartCpdColorPickerDemoModule } from './inputs/colorpicker/colorpickerdemo.module';
import { SmartCpdInputMaskDemoModule } from './inputs/inputmask/inputmaskdemo.module';
import { SmartCpdInputSwitchDemoModule } from './inputs/inputswitch/inputswitchdemo.module';
import { SmartCpdPasswordIndicatorDemoModule } from './inputs/passwordindicator/passwordindicatordemo.module';
import { SmartCpdRatingDemoModule } from './inputs/rating/ratingdemo.module';
import { SmartCpdSelectDemoModule } from './inputs/select/selectdemo.module';
import { SmartCpdListboxDemoModule } from './inputs/listbox/listboxdemo.module';
import { SmartCpdToggleButtonDemoModule } from './inputs/togglebutton/togglebuttondemo.module';
import { SmartCpdEditorDemoModule } from './inputs/editor/editordemo.module';
import { SmartCpdMessagesDemoModule } from './messages/messages/messagesdemo.module';
import { SmartCpdToastDemoModule } from './messages/toast/toastdemo.module';
import { SmartCpdGalleriaDemoModule } from './multimedia/galleria/galleriademo.module';
import { SmartCpdFileUploadDemoModule } from './fileupload/fileupload/fileuploaddemo.module';
import { SmartCpdAccordionDemoModule } from './panel/accordion/accordiondemo.module';
import { SmartCpdPanelDemoModule } from './panel/panel/paneldemo.module';
import { SmartCpdFieldsetDemoModule } from './panel/fieldset/fieldsetdemo.module';
import { SmartCpdToolbarDemoModule } from './panel/toolbar/toolbardemo.module';
import { SmartCpdScrollPanelDemoModule } from './panel/scrollpanel/scrollpaneldemo.module';
import { SmartCpdCardDemoModule } from './panel/card/carddemo.module';
import { SmartCpdFlexGridDemoModule } from './panel/flexgrid/flexgriddemo.module';
import { SmartCpdPickListDemoModule } from './data/picklist/picklistdemo.module';
import { SmartCpdOrderListDemoModule } from './data/orderlist/orderlistdemo.module';
import { SmartCpdPaginatorDemoModule } from './data/paginator/paginatordemo.module';
import { SmartCpdGmapDemoModule } from './data/gmap/gmapdemo.module';
import { SmartCpdOrgChartDemoModule } from './data/orgchart/orgchartdemo.module';
import { SmartCpdCarouselDemoModule } from './data/carousel/carouseldemo.module';
import { SmartCpdDataViewDemoModule } from './data/dataview/dataviewdemo.module';
import { SmartCpdBarchartDemoModule } from './charts/barchart/barchartdemo.module';
import { SmartCpdDoughnutchartDemoModule } from './charts/doughnutchart/doughnutchartdemo.module';
import { SmartCpdLinechartDemoModule } from './charts/linechart/linechartdemo.module';
import { SmartCpdPiechartDemoModule } from './charts/piechart/piechartdemo.module';
import { SmartCpdPolarareachartDemoModule } from './charts/polarareachart/polarareachartdemo.module';
import { SmartCpdRadarchartDemoModule } from './charts/radarchart/radarchartdemo.module';
import { SmartCpdDragDropDemoModule } from './dragdrop/dragdrop/dragdropdemo.module';
import { SmartCpdBlockUIDemoModule } from './misc/blockui/blockuidemo.module';
import { SmartCpdCaptchaDemoModule } from './misc/captcha/captchademo.module';
import { SmartCpdDeferDemoModule } from './misc/defer/deferdemo.module';
import { SmartCpdInplaceDemoModule } from './misc/inplace/inplacedemo.module';
import { SmartCpdProgressBarDemoModule } from './misc/progressbar/progressbardemo.module';
import { SmartCpdValidationDemoModule } from './misc/validation/validationdemo.module';
import { SmartCpdProgressSpinnerDemoModule } from './misc/progressspinner/progressspinnerdemo.module';
@NgModule({
    imports: [
        SmartCpdBlockUIDemoModule,
        SmartCpdCaptchaDemoModule,
        SmartCpdDeferDemoModule,
        SmartCpdInplaceDemoModule,
        SmartCpdProgressBarDemoModule,
        SmartCpdInputMaskDemoModule,
        SmartCpdValidationDemoModule,
        SmartCpdButtonDemoModule,
        SmartCpdSplitbuttonDemoModule,
        SmartCpdInputTextDemoModule,
        SmartCpdInputTextAreaDemoModule,
        SmartCpdInputGroupDemoModule,
        SmartCpdInputMaskDemoModule,
        SmartCpdInputSwitchDemoModule,
        SmartCpdPasswordIndicatorDemoModule,
        SmartCpdRatingDemoModule,
        SmartCpdSelectDemoModule,
        SmartCpdListboxDemoModule,
        SmartCpdToggleButtonDemoModule,
        SmartCpdEditorDemoModule,
        SmartCpdColorPickerDemoModule,
        SmartCpdCheckboxDemoModule,
        SmartCpdKeyFilterDemoModule,
        SmartCpdMessagesDemoModule,
        SmartCpdToastDemoModule,
        SmartCpdGalleriaDemoModule,
        SmartCpdFileUploadDemoModule,
        SmartCpdAccordionDemoModule,
        SmartCpdPanelDemoModule,
        SmartCpdFieldsetDemoModule,
        SmartCpdToolbarDemoModule,
        SmartCpdScrollPanelDemoModule,
        SmartCpdCardDemoModule,
        SmartCpdFlexGridDemoModule,
        SmartCpdBarchartDemoModule,
        SmartCpdDoughnutchartDemoModule,
        SmartCpdLinechartDemoModule,
        SmartCpdPiechartDemoModule,
        SmartCpdPolarareachartDemoModule,
        SmartCpdRadarchartDemoModule,
        SmartCpdDragDropDemoModule,
        SmartCpdConfirmDialogDemoModule,
        SmartCpdLightboxDemoModule,
        SmartCpdTooltipDemoModule,
        SmartCpdOverlayPanelDemoModule,
        SmartCpdSideBarDemoModule,
        SmartCpdDataViewDemoModule,
        SmartCpdOrderListDemoModule,
        SmartCpdPickListDemoModule,
        SmartCpdPaginatorDemoModule,
        SmartCpdOrgChartDemoModule,
        SmartCpdGmapDemoModule,
        SmartCpdCarouselDemoModule,
        SmartCpdProgressSpinnerDemoModule
    ],
    declarations: [],
    entryComponents: [],
    providers: [],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class SmartCpdprimengModule {}
