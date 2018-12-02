function f(){console.log('outside')}

(function(){
    if(false){
        function f(){console.log('inside')}
    }
    f();
}())