const fs=require('fs');
const read = function (fileName){
    return new Promise((resovle,reject)=>{
        fs.readFile(fileName,(err,data)=>{
            if(err){
                reject(err);
            }else{
                resovle(data);
            }
        })
    });
}

function * show(){
    yield read('1.txt')
    yield read('2.txt')
    yield read('3.txt')
}

const s= show()

s.next().value.then(res =>{
    console.log(res.toString())
    return s.next().value;
}).then(res=>{
    console.log(res.toString())
    return s.next().value;
}).then(res=>{
    console.log(res.toString())
})