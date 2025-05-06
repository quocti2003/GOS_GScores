package com.example.gscores.repository;

import com.example.gscores.dto.ScoreDistributionDTO;
import com.example.gscores.model.Subject;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import com.example.gscores.model.Student;

import java.util.List;

public interface StudentRepository extends MongoRepository<Student, String> {
    @Aggregation({
            // Chọn các trường điểm và xử lý null
            "{$project: { " +
                    "toan: { $ifNull: ['$toan', null] }, " +
                    "nguVan: { $ifNull: ['$nguVan', null] }, " +
                    "ngoaiNgu: { $ifNull: ['$ngoaiNgu', null] }, " +
                    "vatLi: { $ifNull: ['$vatLi', null] }, " +
                    "hoaHoc: { $ifNull: ['$hoaHoc', null] }, " +
                    "sinhHoc: { $ifNull: ['$sinhHoc', null] }, " +
                    "lichSu: { $ifNull: ['$lichSu', null] }, " +
                    "diaLi: { $ifNull: ['$diaLi', null] }, " +
                    "gdcd: { $ifNull: ['$gdcd', null] }" +
                    "}}",
            // Tính phân phối cho từng môn học
            "{$facet: { " +
                    "'toan': [{$bucket: { " +
                    "groupBy: '$toan', " +
                    "boundaries: [0, 4, 6, 8, Infinity], " +
                    "default: 'other', " +
                    "output: { count: { $sum: 1 } } " +
                    "}}], " +
                    "'nguVan': [{$bucket: { " +
                    "groupBy: '$nguVan', " +
                    "boundaries: [0, 4, 6, 8, Infinity], " +
                    "default: 'other', " +
                    "output: { count: { $sum: 1 } } " +
                    "}}], " +
                    "'ngoaiNgu': [{$bucket: { " +
                    "groupBy: '$ngoaiNgu', " +
                    "boundaries: [0, 4, 6, 8, Infinity], " +
                    "default: 'other', " +
                    "output: { count: { $sum: 1 } } " +
                    "}}], " +
                    "'vatLi': [{$bucket: { " +
                    "groupBy: '$vatLi', " +
                    "boundaries: [0, 4, 6, 8, Infinity], " +
                    "default: 'other', " +
                    "output: { count: { $sum: 1 } } " +
                    "}}], " +
                    "'hoaHoc': [{$bucket: { " +
                    "groupBy: '$hoaHoc', " +
                    "boundaries: [0, 4, 6, 8, Infinity], " +
                    "default: 'other', " +
                    "output: { count: { $sum: 1 } } " +
                    "}}], " +
                    "'sinhHoc': [{$bucket: { " +
                    "groupBy: '$sinhHoc', " +
                    "boundaries: [0, 4, 6, 8, Infinity], " +
                    "default: 'other', " +
                    "output: { count: { $sum: 1 } } " +
                    "}}], " +
                    "'lichSu': [{$bucket: { " +
                    "groupBy: '$lichSu', " +
                    "boundaries: [0, 4, 6, 8, Infinity], " +
                    "default: 'other', " +
                    "output: { count: { $sum: 1 } } " +
                    "}}], " +
                    "'diaLi': [{$bucket: { " +
                    "groupBy: '$diaLi', " +
                    "boundaries: [0, 4, 6, 8, Infinity], " +
                    "default: 'other', " +
                    "output: { count: { $sum: 1 } } " +
                    "}}], " +
                    "'gdcd': [{$bucket: { " +
                    "groupBy: '$gdcd', " +
                    "boundaries: [0, 4, 6, 8, Infinity], " +
                    "default: 'other', " +
                    "output: { count: { $sum: 1 } } " +
                    "}}] " +
                    "}}",
            // Chuyển đổi kết quả thành định dạng ScoreDistributionDTO
            "{$project: { " +
                    "distributions: { " +
                    "$concatArrays: [ " +
                    "{$map: { input: '$toan', as: 't', in: { subject: ?0, category: { $switch: { branches: [ { case: { $lt: ['$$t._id', 4] }, then: '<4' }, { case: { $and: [{ $gte: ['$$t._id', 4]}, { $lt: ['$$t._id', 6]}] }, then: '4-6' }, { case: { $and: [{ $gte: ['$$t._id', 6]}, { $lt: ['$$t._id', 8]}] }, then: '6-8' }, { case: { $gte: ['$$t._id', 8] }, then: '>=8' } ], default: 'other' }}, count: '$$t.count' }}}," +
                    "{$map: { input: '$nguVan', as: 'nv', in: { subject: ?1, category: { $switch: { branches: [ { case: { $lt: ['$$nv._id', 4] }, then: '<4' }, { case: { $and: [{ $gte: ['$$nv._id', 4]}, { $lt: ['$$nv._id', 6]}] }, then: '4-6' }, { case: { $and: [{ $gte: ['$$nv._id', 6]}, { $lt: ['$$nv._id', 8]}] }, then: '6-8' }, { case: { $gte: ['$$nv._id', 8] }, then: '>=8' } ], default: 'other' }}, count: '$$nv.count' }}}," +
                    "{$map: { input: '$ngoaiNgu', as: 'nn', in: { subject: ?2, category: { $switch: { branches: [ { case: { $lt: ['$$nn._id', 4] }, then: '<4' }, { case: { $and: [{ $gte: ['$$nn._id', 4]}, { $lt: ['$$nn._id', 6]}] }, then: '4-6' }, { case: { $and: [{ $gte: ['$$nn._id', 6]}, { $lt: ['$$nn._id', 8]}] }, then: '6-8' }, { case: { $gte: ['$$nn._id', 8] }, then: '>=8' } ], default: 'other' }}, count: '$$nn.count' }}}," +
                    "{$map: { input: '$vatLi', as: 'vl', in: { subject: ?3, category: { $switch: { branches: [ { case: { $lt: ['$$vl._id', 4] }, then: '<4' }, { case: { $and: [{ $gte: ['$$vl._id', 4]}, { $lt: ['$$vl._id', 6]}] }, then: '4-6' }, { case: { $and: [{ $gte: ['$$vl._id', 6]}, { $lt: ['$$vl._id', 8]}] }, then: '6-8' }, { case: { $gte: ['$$vl._id', 8] }, then: '>=8' } ], default: 'other' }}, count: '$$vl.count' }}}," +
                    "{$map: { input: '$hoaHoc', as: 'hh', in: { subject: ?4, category: { $switch: { branches: [ { case: { $lt: ['$$hh._id', 4] }, then: '<4' }, { case: { $and: [{ $gte: ['$$hh._id', 4]}, { $lt: ['$$hh._id', 6]}] }, then: '4-6' }, { case: { $and: [{ $gte: ['$$hh._id', 6]}, { $lt: ['$$hh._id', 8]}] }, then: '6-8' }, { case: { $gte: ['$$hh._id', 8] }, then: '>=8' } ], default: 'other' }}, count: '$$hh.count' }}}," +
                    "{$map: { input: '$sinhHoc', as: 'sh', in: { subject: ?5, category: { $switch: { branches: [ { case: { $lt: ['$$sh._id', 4] }, then: '<4' }, { case: { $and: [{ $gte: ['$$sh._id', 4]}, { $lt: ['$$sh._id', 6]}] }, then: '4-6' }, { case: { $and: [{ $gte: ['$$sh._id', 6]}, { $lt: ['$$sh._id', 8]}] }, then: '6-8' }, { case: { $gte: ['$$sh._id', 8] }, then: '>=8' } ], default: 'other' }}, count: '$$sh.count' }}}," +
                    "{$map: { input: '$lichSu', as: 'ls', in: { subject: ?6, category: { $switch: { branches: [ { case: { $lt: ['$$ls._id', 4] }, then: '<4' }, { case: { $and: [{ $gte: ['$$ls._id', 4]}, { $lt: ['$$ls._id', 6]}] }, then: '4-6' }, { case: { $and: [{ $gte: ['$$ls._id', 6]}, { $lt: ['$$ls._id', 8]}] }, then: '6-8' }, { case: { $gte: ['$$ls._id', 8] }, then: '>=8' } ], default: 'other' }}, count: '$$ls.count' }}}," +
                    "{$map: { input: '$diaLi', as: 'dl', in: { subject: ?7, category: { $switch: { branches: [ { case: { $lt: ['$$dl._id', 4] }, then: '<4' }, { case: { $and: [{ $gte: ['$$dl._id', 4]}, { $lt: ['$$dl._id', 6]}] }, then: '4-6' }, { case: { $and: [{ $gte: ['$$dl._id', 6]}, { $lt: ['$$dl._id', 8]}] }, then: '6-8' }, { case: { $gte: ['$$dl._id', 8] }, then: '>=8' } ], default: 'other' }}, count: '$$dl.count' }}}," +
                    "{$map: { input: '$gdcd', as: 'gd', in: { subject: ?8, category: { $switch: { branches: [ { case: { $lt: ['$$gd._id', 4] }, then: '<4' }, { case: { $and: [{ $gte: ['$$gd._id', 4]}, { $lt: ['$$gd._id', 6]}] }, then: '4-6' }, { case: { $and: [{ $gte: ['$$gd._id', 6]}, { $lt: ['$$gd._id', 8]}] }, then: '6-8' }, { case: { $gte: ['$$gd._id', 8] }, then: '>=8' } ], default: 'other' }}, count: '$$gd.count' }}}" +
                    "]" +
                    "}" +
                    "}}",
            // Chuyển đổi thành danh sách ScoreDistributionDTO
            "{$unwind: '$distributions'}",
            "{$group: { " +
                    "_id: '$distributions.subject', " +
                    "distribution: { $push: { k: '$distributions.category', v: '$distributions.count' } } " +
                    "}}",
            "{$project: { " +
                    "_id: 0, " +
                    "subject: '$_id', " +
                    "distribution: { $arrayToObject: '$distribution' } " +
                    "}}"
    })
    List<ScoreDistributionDTO> getScoreDistributions(
            String toan, String nguVan, String ngoaiNgu, String vatLi, String hoaHoc,
            String sinhHoc, String lichSu, String diaLi, String gdcd
    );
}
