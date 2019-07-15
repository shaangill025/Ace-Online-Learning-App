import { AfterViewInit, Component, ElementRef, OnInit, QueryList, ViewChildren } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { IQuizApp } from 'app/shared/model/quiz-app.model';
import { IQuiz } from 'app/shared/model/quiz.model';
import { IQuestion } from 'app/shared/model/question.model';
import { IChoice } from 'app/shared/model/choice.model';
import { CustomerService } from 'app/entities/customer';
import { IBookmark } from 'app/shared/model/bookmark.model';
import { ICourse } from 'app/shared/model/course.model';
import { ITimeCourseLog } from 'app/shared/model/time-course-log.model';
import { TimeCourseLogService } from 'app/entities/time-course-log';
import { Account, Principal, UserService } from 'app/core';
import { SectionHistoryService } from 'app/entities/section-history';
import { ISectionHistory } from 'app/shared/model/section-history.model';
import { QuizHistoryService } from 'app/entities/quiz-history';
import { IQuizHistory } from 'app/shared/model/quiz-history.model';
import * as moment from 'moment';
import { CertificateService } from 'app/entities/certificate';
import { ICertificate } from 'app/shared/model/certificate.model';
import { ICustomer } from 'app/shared/model/customer.model';
import { IQuestionHistory } from 'app/shared/model/question-history.model';
import { QuestionHistoryService } from 'app/entities/question-history';
import { QuestionService } from 'app/entities/question';
import { DATE_TIME_FORMAT } from 'app/shared';
import { SectionService } from 'app/entities/section';
import { SERVER_API_URL } from 'app/app.constants';
import { NgxSpinnerService } from 'ngx-spinner';
import { CourseHistoryService } from 'app/entities/course-history';
import { ICourseHistory } from 'app/shared/model/course-history.model';

@Component({
    selector: 'jhi-quiz-app-detail',
    templateUrl: './quiz-app-detail.component.html'
})
export class QuizAppDetailComponent implements OnInit, AfterViewInit {
    quizApp: IQuizApp;
    quiz: IQuiz;
    questionList: IQuestion[];
    currentQuestion: number;
    proceed: Boolean;
    isselected: Boolean;
    message: string;
    restudy: string;
    value: number;
    ellapsed = 0;
    ellapsedTime: string;
    pause = false;
    startDate: Date;
    account: Account;
    custEmail: string;
    reqdCourse: ICourse;
    currCourse: ICourse;
    logs: ITimeCourseLog;
    tempQuizHist: IQuizHistory;
    currentAccount: any;
    customer: ICustomer;
    certificate: ICertificate;
    quesHistory: IQuestionHistory;
    courseHist: ICourseHistory;
    hasCompleted = false;
    completed = 0;
    accessMessage = '';
    accessFlag: boolean;

    @ViewChildren('radioBtn') checkboxes: QueryList<ElementRef>;
    constructor(
        private activatedRoute: ActivatedRoute,
        private router: Router,
        private userService: UserService,
        private customerService: CustomerService,
        private timeCourseLogService: TimeCourseLogService,
        private principal: Principal,
        private sectionHistoryService: SectionHistoryService,
        private quizHistoryService: QuizHistoryService,
        private certificateService: CertificateService,
        private questionHistoryService: QuestionHistoryService,
        private questionService: QuestionService,
        private sectionService: SectionService,
        private spinner: NgxSpinnerService,
        private courseHistoryService: CourseHistoryService
    ) {
        this.currentQuestion = 1;
        this.logs = {};
        this.tempQuizHist = {};
        this.quesHistory = {};
        this.certificate = {};
    }

    ngAfterViewInit(): void {
        this.spinner.show();
        setTimeout(() => {
            /** spinner ends after 5 seconds */
            this.spinner.hide();
        }, 2000);
    }

    ngOnInit() {
        setInterval(() => {
            if (!this.hasCompleted) {
                this.ticks();
            }
        }, 1000);

        this.activatedRoute.data.subscribe(({ quizApp }) => {
            this.quizApp = quizApp;
            this.currCourse = this.quizApp.currSection.course;
        });
        this.principal.identity().then(account => {
            this.currentAccount = account;
            this.custEmail = this.currentAccount.email;
            this.userService.getuserbylogin(this.currentAccount.login).subscribe(userId => {
                this.customerService.getuser(userId).subscribe(customer => {
                    this.customer = customer;
                    this.courseHistoryService
                        .getbycustomerandcourse(this.quizApp.currSection.course.id, customer.id)
                        .subscribe(courseHist => {
                            this.courseHist = courseHist;
                            this.timeCourseLogService.findmodified(courseHist.id, customer.id).subscribe(log => {
                                this.logs = log;
                            });
                        });
                });
            });
        });
        this.quiz = this.quizApp.quiz;
        this.questionService.findbyquiz(this.quiz.id).subscribe(data => {
            this.questionList = data.body;
        });
        this.isselected = false;
        this.proceed = false;
        this.clearOptions();
        this.startDate = new Date();
        this.quizHistoryService.lastattmpt(this.quizApp.currSection.id, this.customer.id).subscribe(flag => {
            this.accessFlag = flag;
            if (!this.accessFlag) {
                this.accessMessage =
                    'The API gateway has encountered an issue when handling your request, kindly contact support for further assistance.';
            }
        });
    }

    onSelect(question: IQuestion, option: IChoice) {
        question.choices.forEach(x => {
            if (x.id === option.id) {
                this.isselected = true;
            }
        });
        if (this.isselected) {
            if (option.isanswer) {
                this.proceed = true;
                this.message = 'Q:' + this.currentQuestion + ' has been answered correctly, moving to the next question';
                if (this.currentQuestion >= this.questionList.length) {
                    this.completed = this.ellapsed;
                    this.hasCompleted = true;
                    this.value = this.currentQuestion / this.questionList.length * 100;
                    this.userService.getuserbylogin(this.currentAccount.login).subscribe(userId => {
                        this.customerService.getuser(userId).subscribe(customer => {
                            this.customer = customer;
                            this.reqdCourse = this.quizApp.currSection.course;
                            this.logs.timespent += this.ellapsed;
                            console.log('Updating time-course-log, before: ' + this.logs.timespent);
                            this.timeCourseLogService.update(this.logs).subscribe(updated => {
                                this.logs = updated.body;
                            });
                            this.tempQuizHist.customer = this.customer;
                            this.tempQuizHist.quiz = this.quiz;
                            this.tempQuizHist.passed = true;
                            this.tempQuizHist.start = moment(new Date(), DATE_TIME_FORMAT);
                            this.quizHistoryService.create(this.tempQuizHist).subscribe(quizHist => {
                                console.log('quiz history created' + quizHist.body.id);
                            });
                        });
                    });
                    if (this.quizApp.newSection === null) {
                        this.userService.getuserbylogin(this.currentAccount.login).subscribe(userId => {
                            this.customerService.getuser(userId).subscribe(customer => {
                                this.sectionHistoryService.getcount(customer.id, this.quizApp.currSection.course.id).subscribe(listHist => {
                                    const count1: number = listHist.body.length;
                                    this.sectionService.getcountbycourse(this.quizApp.currSection.course.id).subscribe(listSect => {
                                        const count2: number = listSect.body.length;
                                        if (count1 === count2) {
                                            this.activatedRoute.data.subscribe(({ quizApp }) => {
                                                this.certificate.courseHistory = this.courseHist;
                                            });
                                            this.certificate.timestamp = moment(new Date(), DATE_TIME_FORMAT);
                                            this.certificate.customer = customer;
                                            this.courseHistoryService
                                                .getbycustomerandcourse(this.quizApp.currSection.course.id, customer.id)
                                                .subscribe(courseHist => {
                                                    courseHist.iscompleted = true;
                                                    courseHist.isactive = false;
                                                    courseHist.lastactivedate = moment(new Date(), DATE_TIME_FORMAT);
                                                    this.courseHistoryService.update(courseHist).subscribe(updated => {
                                                        console.log('Course History has been updated @' + updated.body.lastactivedate);
                                                    });
                                                });
                                            this.certificateService
                                                .getbycustomercourse(this.courseHist.id, this.customer.id)
                                                .subscribe(cert => {
                                                    if (cert.id === -1) {
                                                        this.certificateService.create(this.certificate).subscribe(tempCert => {
                                                            this.message =
                                                                'You have successfuly completed all the modules and quizes for this course, a certificate ' +
                                                                'will be shortly generated [accessible on dashboard] and sent via email';
                                                            setTimeout(
                                                                function() {
                                                                    this.router.navigateByUrl(
                                                                        SERVER_API_URL +
                                                                            'certificate/' +
                                                                            tempCert.body.id.toString() +
                                                                            '/view'
                                                                    );
                                                                }.bind(this),
                                                                6000
                                                            );
                                                        });
                                                    } else {
                                                        this.message =
                                                            'You had already completed this course and a certificate for it was already issued. You will be redirected' +
                                                            ' to the Dashboard shortly';
                                                        setTimeout(
                                                            function() {
                                                                this.router.navigateByUrl(SERVER_API_URL + 'dashboards');
                                                            }.bind(this),
                                                            3000
                                                        );
                                                    }
                                                });
                                        }
                                    });
                                });
                            });
                        });
                    } else {
                        this.message =
                            'You have successfully completed the' +
                            this.quizApp.quiz.name +
                            'quiz, and shortly you will be redirected to Course:' +
                            this.quizApp.currSection.course.normCourses +
                            ', Module: ' +
                            this.quizApp.newSection.normSection;
                        this.courseHistoryService
                            .getbycustomerandcourse(this.quizApp.currSection.course.id, this.customer.id)
                            .subscribe(courseHist => {
                                courseHist.lastactivedate = moment(new Date(), DATE_TIME_FORMAT);
                                this.courseHistoryService.update(courseHist).subscribe(updated => {
                                    console.log('Course History has been updated @' + updated.body.lastactivedate);
                                });
                            });
                        setTimeout(
                            function() {
                                this.router.navigateByUrl(SERVER_API_URL + 'section/' + this.quizApp.newSection.id.toString() + '/view');
                            }.bind(this),
                            6000
                        );
                    }
                } else {
                    setTimeout(() => {
                        this.currentQuestion++;
                        this.value = (this.currentQuestion - 1) / this.questionList.length * 100;
                        /**if (this.questionList.length !== this.currentQuestion) {
                            this.value = (this.currentQuestion - 1) / this.questionList.length * 100;
                        } else {
                            this.value = (this.currentQuestion) / this.questionList.length * 100
                        }*/
                        this.proceed = false;
                        this.isselected = false;
                        this.clearOptions();
                    }, 3000);
                }
                /**this.isselected = false;*/
                this.quesHistory.correct = true;
                this.quesHistory.customer = this.customer;
                this.quesHistory.question = question;
                this.quesHistory.timestamp = moment(new Date());
                this.questionHistoryService.create(this.quesHistory).subscribe(quesHist => {
                    console.log(quesHist);
                    this.quesHistory = {};
                });
            } else {
                this.proceed = false;
                this.message =
                    'You have selected the wrong option for this question, kindly review the following the restudy material to attempt again and proceed further';
                this.restudy = question.restudy;
                setTimeout(
                    function() {
                        this.value = (this.currentQuestion - 1) / this.questionList.length * 100;
                        this.isselected = false;
                        this.clearOptions();
                    }.bind(this),
                    6000
                );
                this.quesHistory.correct = false;
                this.quesHistory.customer = this.customer;
                this.quesHistory.question = question;
                this.quesHistory.timestamp = moment(new Date());
                this.questionHistoryService.create(this.quesHistory).subscribe(quesHist => {
                    console.log(quesHist);
                    this.quesHistory = {};
                });
            }
        } else {
            this.message = 'You have not selected any options for this question';
            this.proceed = false;
        }
    }

    clearOptions() {
        this.checkboxes.forEach(element => {
            element.nativeElement.checked = false;
        });
    }

    previousState() {
        window.history.back();
    }

    ticks() {
        this.ellapsed++;
        this.ellapsedTime = this.parseTime(this.ellapsed);
        console.log('Ellapsed Time - QuizApp' + this.ellapsedTime);
    }

    /**ticksSecond() {
        if (this.api.isCompleted) {
            this.pause = true;
        } else {
            if (this.pause === false) {
                this.ellapsed++;
                this.ellapsedTime = this.parseTime(this.ellapsed);
                /**this.ellapsed = this.api.currentTime;
                 this.ellapsedTime = this.parseTime(this.ellapsed);
            }
        }
    }*/

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
}
