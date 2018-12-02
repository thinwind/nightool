const fs=require('fs')

const read = function(fileName){
    return new Promise((resole,reject)=>{
        console.log('start'+fileName)
        fs.readFile(fileName,(err,data)=>{
            if(err){
                reject(err)
            }else{
                resole(data)
            }
        })
    });
}

async function readByAsync(){
    let a1= await read('1.txt');
    console.log('after a1')
    let a2 = await read('2.txt');
    console.log('after a3')
    let a3 = await read('3.txt')
    console.log('after a3')

    console.log(a2.toString())
    console.log(a1.toString())
    console.log(a3.toString())
    console.log('complete')
}

readByAsync();