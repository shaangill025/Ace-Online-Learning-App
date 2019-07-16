import { AfterViewInit, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { JhiDataUtils } from 'ng-jhipster';
import { ISection } from 'app/shared/model/section.model';
import { VgAPI } from 'videogular2/core';
import { BookmarkService } from 'app/entities/bookmark';
import { TimeCourseLogService } from 'app/entities/time-course-log';
import { ITimeCourseLog } from 'app/shared/model/time-course-log.model';
import { IBookmark } from 'app/shared/model/bookmark.model';
import { Account, AccountService, Principal } from 'app/core';
import { CustomerService } from 'app/entities/customer';
import { UserService } from 'app/core';
import { CourseService } from 'app/entities/course';
import { interval, Observable } from 'rxjs';
import { templateJitUrl } from '@angular/compiler';
import { numberOfBytes } from 'ng-jhipster/src/directive/number-of-bytes';
import { logsRoute } from 'app/admin';
import { Customer, ICustomer } from 'app/shared/model/customer.model';
import { __values } from 'tslib';
import { Course } from 'app/shared/model/course.model';
import { SectionHistoryService } from 'app/entities/section-history';
import { ISectionHistory } from 'app/shared/model/section-history.model';
import * as moment from 'moment';
import { IQuizApp } from 'app/shared/model/quiz-app.model';
import { QuestionService } from 'app/entities/question';
import { QuizAppService } from 'app/entities/quiz-app';
import { DATE_TIME_FORMAT } from 'app/shared';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { FileManagerService } from 'app/entities/file-manager';
import { NgxSpinnerService } from 'ngx-spinner';
import { CourseHistoryService } from 'app/entities/course-history';
import { ICourseHistory } from 'app/shared/model/course-history.model';
import { Moment } from 'moment';
import {accountAliasListType} from "aws-sdk/clients/iam";

@Component({
    selector: 'jhi-section-detail',
    templateUrl: './section-detail.component.html'
})
export class SectionDetailComponent implements OnInit {
    section: ISection;
    pageNum: number;
    lastpageNum: number;
    test: any;
    pdflink: string;
    api: VgAPI;
    ellapsedTime = '00:00';
    completed: string;
    ellapsed = 0;
    prevEllapsed = 0;
    bookmarks: IBookmark[];
    pause = false;
    startDate: Date;
    startHistDate: Moment;
    counter = 0;
    diff: any;
    isComplete = false;
    comingAgainFlag: Boolean;
    arrayBuffer: any;
    custEmail: string;
    textCont: string;
    completedTime = 0;
    private prevHistory: ISectionHistory;
    counter3 = 0;
    logspdf: ITimeCourseLog;
    customer: ICustomer;
    tempQuizApp: IQuizApp;
    completeFlag = false;
    isSaving = false;
    accessErrorFlag = true;
    timeCourseMoment: Moment;
    accessFlag = false;
    flagCheck = false;
    courseIsActive = false;
    courseAccess = false;
    pdfSessionLoading = false;
    showButton = false;
    courseHistory: ICourseHistory;

    constructor(
        private dataUtils: JhiDataUtils,
        private activatedRoute: ActivatedRoute,
        private router: Router,
        private bookmarkService: BookmarkService,
        private timeCourseLogService: TimeCourseLogService,
        // private account: Account,
        private customerService: CustomerService,
        private userService: UserService,
        private principal: Principal,
        private sectionHistoryService: SectionHistoryService,
        private questionService: QuestionService,
        private quizAppService: QuizAppService,
        private fileManagerService: FileManagerService,
        //private spinner: NgxSpinnerService,
        private courseHistoryService: CourseHistoryService
    ) {
        this.logspdf = {};
        this.tempQuizApp = {};
    }

    private subscribeToSaveResponse(result: Observable<HttpResponse<ISectionHistory>>) {
        result.subscribe((res: HttpResponse<ISectionHistory>) => this.onSaveSuccess(), (res: HttpErrorResponse) => this.onSaveError());
    }

    private onSaveSuccess() {
        this.isSaving = false;
    }

    private onSaveError() {
        this.isSaving = false;
    }

    ngOnInit() {
        this.activatedRoute.data.subscribe(({ section }) => {
            this.section = section;
            if (this.section.textcontent !== null) {
                // this.textCont = this.section.textcontent.toString().slice(3, -4);
                this.textCont = this.section.textcontent.toString().replace(/<[^>]+>/g, '');
            }
        });
        this.bookmarkService.getsection(this.section.id).subscribe(data => {
            this.bookmarks = data.body;
        });
        this.principal.identity().then(account => {
            this.custEmail = account.email;
            this.userService.getuserbylogin(account.login).subscribe(userId => {
                this.customerService.getuser(account.login).subscribe(customer => {
                    this.customer = customer;
                    this.sectionHistoryService.lastwatched(this.section.id, customer.id).subscribe(flag => {
                        console.log('flag - ' + flag);
                        console.log(customer.id);
                        console.log(this.section.course.id);
                        this.courseHistoryService.getbycustomerandcourse(this.section.course.id, customer.id).subscribe(courseHist => {
                            this.courseAccess = courseHist.access;
                            this.courseIsActive = courseHist.isactive;
                            this.courseHistory = courseHist;
                            console.log('course history isactive - ' + this.courseIsActive);
                            console.log('course history access - ' + this.courseAccess);
                            if (flag === false || !this.courseAccess || !this.courseIsActive) {
                                this.accessErrorFlag = true;
                                console.log('Access Error Flag: ' + this.accessErrorFlag);
                            } else {
                                this.accessFlag = true;
                                this.accessErrorFlag = false;
                                this.sectionHistoryService.getbycustomer(customer.id, this.section.course.id).subscribe(secHist => {
                                    this.prevHistory = secHist;
                                    console.log('section history id is : ' + secHist.id);
                                    this.startHistDate = secHist.startdate;
                                    this.startDate = new Date();
                                    this.timeCourseLogService.findmodified(this.courseHistory.id, this.customer.id).subscribe(log => {
                                        const temp: ITimeCourseLog = log;
                                        console.log('Inside sectionDetail component, time course find modified, id is : ' + log.id);
                                        const recordDateTemp: Moment = temp.recorddate;
                                        const startDateTemp: Moment = courseHist.startdate;
                                        if (temp.id === -1) {
                                            this.logspdf.courseHistory = courseHist;
                                            this.logspdf.customer = this.customer;
                                            this.logspdf.recorddate = moment(new Date(), DATE_TIME_FORMAT);
                                            this.logspdf.timespent = 0;
                                            this.timeCourseLogService.create(this.logspdf).subscribe(updated => {
                                                this.logspdf = updated.body;
                                                this.timeCourseMoment = this.logspdf.recorddate;
                                                console.log('Inside sectionDetail component, new id is : ' + updated.body.id);
                                            });
                                        } else {
                                            this.logspdf = log;
                                            console.log('Inside sectionDetail component, existing id is : ' + log.id);
                                        }
                                    });
                                    if (this.section.type === 'pdf') {
                                        this.pdflink = this.section.pdfUrl;
                                        this.lastpageNum = this.section.totalPages;
                                        if (this.prevHistory.section.id === this.section.id) {
                                            if (this.prevHistory.stamp !== 0) {
                                                this.pdfSessionLoading = true;
                                                setTimeout(() => {
                                                    console.log('Stamp - Timer' + this.prevHistory.stamp);
                                                    this.pdfSessionLoading = false;
                                                    this.showButton = true;
                                                    this.gotoSlide(this.prevHistory.stamp);
                                                    this.pageNum = this.prevHistory.stamp;
                                                }, 10000);
                                            } else {
                                                this.pageNum = 1;
                                                this.sectionHistoryService
                                                    .updatepersistance(this.prevHistory.id, this.pageNum)
                                                    .subscribe(dataTemp2 => {
                                                        //console.log('Stamp is' + dataTemp2.stamp);
                                                    });
                                            }
                                        } else {
                                            this.prevHistory.section = this.section;
                                            this.prevHistory.stamp = 1;
                                            this.sectionHistoryService
                                                .update(this.prevHistory)
                                                .subscribe(dataTemp2 => {
                                                    //console.log('Stamp is' + dataTemp2.stamp);
                                                });
                                        }
                                        this.flagCheck = true;
                                        setInterval(() => {
                                            if (this.router.url.includes('/section', 0)) {
                                                if (!this.completeFlag) {
                                                    this.ticks();
                                                }
                                                if (this.pageNum === this.lastpageNum) {
                                                    this.completeFlag = true;
                                                    if (this.counter3 === 0) {
                                                        this.completedTime = this.ellapsed;
                                                        this.completed = this.parseTime(this.completedTime);
                                                        this.logspdf.timespent += this.ellapsed % 60;
                                                        this.logspdf.recorddate = this.timeCourseMoment;
                                                        console.log('Updating time-course-log, before: ' + this.logspdf.timespent);
                                                        const checkUrl1 = this.router.url.includes('/section', 0);
                                                        const checkUrl2 = this.router.url.includes('/view', 0);
                                                        if (checkUrl1 && checkUrl2 && !this.completeFlag) {
                                                            this.timeCourseLogService.update(this.logspdf).subscribe(log => {
                                                                this.logspdf = log.body;
                                                                console.log('Updating time-course-log, before: ' + this.logspdf.timespent);
                                                            });
                                                        }
                                                        this.counter3++;
                                                    }
                                                }
                                                if (this.ellapsed % 60 === 0) {
                                                    this.prevHistory.stamp = this.pageNum;
                                                    this.sectionHistoryService
                                                        .updatepersistance(this.prevHistory.id, this.prevHistory.stamp)
                                                        .subscribe(dataTemp2 => {
                                                            //console.log('Stamp is' + dataTemp2.stamp);
                                                        });
                                                    this.logspdf.timespent += 60;
                                                    this.logspdf.recorddate = this.timeCourseMoment;
                                                    console.log('Updating time-course-log, before: ' + this.logspdf.timespent);
                                                    const checkUrl1 = this.router.url.includes('/section', 0);
                                                    const checkUrl2 = this.router.url.includes('/view', 0);
                                                    if (checkUrl1 && checkUrl2 && !this.completeFlag) {
                                                        this.timeCourseLogService.update(this.logspdf).subscribe(log => {
                                                            this.logspdf = log.body;
                                                            console.log('Updating time-course-log, after: ' + this.logspdf.timespent);
                                                        });
                                                    }
                                                }
                                            }
                                        }, 1000);
                                    } else {
                                        console.log('checking stamp value from persistance functions' + this.prevHistory.stamp);
                                        if (this.prevHistory.section.id === this.section.id) {
                                            if (this.prevHistory.stamp !== 0) {
                                                this.ellapsed = this.prevHistory.stamp;
                                                this.api.seekTime(this.prevHistory.stamp, false);
                                                this.onPlay();
                                            } else {
                                                this.ellapsed = 0;
                                                this.sectionHistoryService
                                                    .updatepersistance(this.prevHistory.id, this.ellapsed)
                                                    .subscribe(dataTemp2 => {
                                                        //console.log('Stamp is' + dataTemp2.stamp);
                                                    });
                                                this.onPlay();
                                            }
                                        } else {
                                            this.prevHistory.section = this.section;
                                            this.prevHistory.stamp = 0;
                                            this.sectionHistoryService
                                                .update(this.prevHistory)
                                                .subscribe(dataTemp2 => {
                                                    //console.log('Stamp is' + dataTemp2.stamp);
                                                });
                                        }
                                        this.flagCheck = true;
                                        setInterval(() => {
                                            if (this.router.url.includes('/section', 0)) {
                                                console.log('Required State -' + this.api.state.toString());
                                                if (!this.completeFlag && this.api.state.toString() === 'playing') {
                                                    this.ticks();
                                                    if (this.ellapsed % 30 === 0 && !this.completeFlag) {
                                                        this.prevHistory.stamp = this.ellapsed;
                                                        this.sectionHistoryService
                                                            .updatepersistance(this.prevHistory.id, this.prevHistory.stamp)
                                                            .subscribe(dataTemp2 => {
                                                                console.log('Stamp is' + dataTemp2.stamp);
                                                            });
                                                    }
                                                    if (this.ellapsed % 60 === 0) {
                                                        this.logspdf.timespent += 60;
                                                        this.logspdf.recorddate = this.timeCourseMoment;
                                                        console.log('Updating time-course-log, before: ' + this.logspdf.timespent);
                                                        const checkUrl1 = this.router.url.includes('/section', 0);
                                                        const checkUrl2 = this.router.url.includes('/view', 0);
                                                        if (checkUrl1 && checkUrl2 && !this.completeFlag) {
                                                            this.timeCourseLogService.update(this.logspdf).subscribe(log => {
                                                                this.logspdf = log.body;
                                                                console.log('Updating time-course-log, after: ' + this.logspdf.timespent);
                                                            });
                                                        }
                                                    }
                                                }
                                            }
                                            if (this.api.state.toString() === 'ended') {
                                                this.completeFlag = true;
                                            }
                                            if (this.completeFlag && this.counter3 === 0) {
                                                this.completedTime = this.ellapsed;
                                                this.completed = this.parseTime(this.completedTime);
                                                this.counter3++;
                                                this.logspdf.timespent += this.ellapsed % 60;
                                                this.logspdf.recorddate = this.timeCourseMoment;
                                                console.log('Updating time-course-log, before: ' + this.logspdf.timespent);
                                                const checkUrl1 = this.router.url.includes('/section', 0);
                                                const checkUrl2 = this.router.url.includes('/view', 0);
                                                if (checkUrl1 && checkUrl2 && !this.completeFlag) {
                                                    this.timeCourseLogService.update(this.logspdf).subscribe(log => {
                                                        this.logspdf = log.body;
                                                        console.log('Updating time-course-log, after: ' + this.logspdf.timespent);
                                                    });
                                                }
                                                if ((this.comingAgainFlag = false)) {
                                                    this.onPause();
                                                }
                                            }
                                        }, 1000);
                                    }
                                });
                            }
                        });
                    });
                });
            });
        });
    }

    loadPersistance() {
        //this.gotoSlide(this.prevHistory.stamp);
        this.pageNum++;
        this.pageNum--;
        this.showButton = false;
    }

    resetShowButton() {
        this.showButton = false;
    }

    onPause() {
        this.pause = true;
        this.api.pause();
    }

    referSlide(slide: number) {
        this.pageNum = slide;
    }

    referVideo2(second: number) {
        this.api.seekTime(second);
    }

    referVideo(time: string) {
        const hrs = time.substring(0, 2);
        const min = time.substring(3, 5);
        const sec = time.substring(6, 8);
        const hrsNum = Number(hrs);
        const minNum = Number(min);
        const secNum = Number(sec);
        this.api.seekTime(hrsNum * 3600 + minNum * 60 + secNum);
    }

    onPlay() {
        if (this.isComplete) {
            this.comingAgainFlag = true;
        }
        this.prevEllapsed = this.ellapsed;
        this.startDate = new Date();
        this.pause = false;
        this.api.play();
    }

    onStepBackward30() {
        if (this.isComplete) {
            this.comingAgainFlag = true;
        }
        if (this.ellapsed <= 30) {
            this.onReset();
        } else {
            /**this.ellapsed = this.ellapsed - 30;*/
            this.api.seekTime(this.ellapsed - 30, false);
            this.onPlay();
        }
    }

    onStepBackward60() {
        if (this.isComplete) {
            this.comingAgainFlag = true;
        }
        if (this.ellapsed <= 60) {
            this.onReset();
        } else {
            /**this.ellapsed = this.ellapsed - 60;*/
            this.api.seekTime(this.ellapsed - 60, false);
            this.onPlay();
        }
    }

    onStepBackward300() {
        if (this.isComplete) {
            this.comingAgainFlag = true;
        }
        if (this.ellapsed <= 300) {
            this.onReset();
        } else {
            /**this.ellapsed = this.ellapsed - 300;*/
            this.api.seekTime(this.ellapsed - 300, false);
            this.onPlay();
        }
    }

    onReset() {
        if (this.isComplete) {
            this.comingAgainFlag = true;
        }
        this.api.seekTime(0, false);
        this.onPlay();
    }

    ticks() {
        if (this.pause === false || this.section.type === 'pdf') {
            this.ellapsed++;
            this.ellapsedTime = this.parseTime(this.ellapsed);
        }
    }

    parseTime(totalSeconds: number) {
        let hrs: string | number = Math.floor(totalSeconds / 3600);
        if (Number(hrs) < 1) {
            let mins: string | number = Math.floor(totalSeconds / 60);
            let secs: string | number = Math.round(totalSeconds % 60);
            mins = (mins < 10 ? '0' : '') + mins;
            secs = (secs < 10 ? '0' : '') + secs;
            hrs = (hrs < 10 ? '0' : '') + hrs;
            return mins + ':' + secs;
        } else {
            let mins: string | number = Math.floor((totalSeconds % 3600) / 60);
            let secs: string | number = Math.round(totalSeconds % 60);
            mins = (mins < 10 ? '0' : '') + mins;
            secs = (secs < 10 ? '0' : '') + secs;
            hrs = (hrs < 10 ? '0' : '') + hrs;
            return hrs + ':' + mins + ':' + secs;
        }
    }

    gotoSlide(slide: number) {
        this.pageNum = slide;
    }

    onPlayerReady(api: VgAPI) {
        this.api = api;
    }

    nextPage() {
        this.pageNum++;
    }

    prevPage() {
        this.pageNum--;
    }

    moveToQuiz() {
        this.flagCheck = false;
        this.prevHistory.watched = true;
        this.sectionHistoryService.update(this.prevHistory).subscribe(history => {
            this.tempQuizApp.currSection = history.body.section;
            this.tempQuizApp.newSection = history.body.section.quiz.newSection;
            this.tempQuizApp.quiz = history.body.section.quiz;
            this.courseHistoryService.getbycustomerandcourse(this.section.course.id, this.customer.id).subscribe(courseHist => {
                courseHist.lastactivedate = moment(new Date(), DATE_TIME_FORMAT);
                this.courseHistoryService.update(courseHist).subscribe(updated => {
                    console.log('Course History has been updated @' + updated.body.lastactivedate);
                });
            });
            this.quizAppService.create(this.tempQuizApp).subscribe(quiz => {
                this.router.navigateByUrl('quiz-app/' + quiz.body.id.toString() + '/view');
            });
        });
    }
}
