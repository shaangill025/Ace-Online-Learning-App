import { ISection } from 'app/shared/model//section.model';

export interface IFileManager {
    id?: number;
    fileContentType?: string;
    file?: any;
    section?: ISection;
}

export class FileManager implements IFileManager {
    constructor(public id?: number, public fileContentType?: string, public file?: any, public section?: ISection) {}
}
