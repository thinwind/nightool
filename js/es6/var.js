var a=[];
for(var i=0;i<10;i++){
    a[i]=function(){
        console.log(i);
    }
}

a[6]();

a=[];
for(var j=0;j<10;j++){
    a[j]=function(index){
        return function(){
            console.log(index);
        }
    }(j);
}

a[6]()

a=[];
for(let m=0;m<10;m++){
    a[m]=function(){
        console.log(m);
    };
}
a[6]()