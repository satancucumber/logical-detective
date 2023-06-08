<template>
  <div>
    <p></p>
    <h5>Собранная информация</h5>
    <p></p>
    <div style="border: solid gray">
      <div v-for="formula in formulas" v-bind:key="formula.id">
        <div>
          {{ formula.desform }}
        </div>
      </div>
    </div>
    <p></p>
    <h5>Преобразование выражений в КНФ</h5>
    <p></p>
    <div v-for="(list, i) in resFormulas" v-bind:key="i">
      <div>
        <div style="border: solid">
          {{ formulas[i].desform }}
          <span>
            <b-button type="button" class="btn btn-light" @click="changeShow(i)">
              <b-icon icon="chevron-down"></b-icon>
            </b-button>
          </span>
        </div>

        <div style="border: solid" v-show="show(i)">
        <div v-for="res in list" v-bind:key="res.id">
          <div>
            <p></p>
            <div>
              <span>
                {{ res.description }}
              </span>
            </div>
            <p></p>
            <div >
              <span style="border: solid gray">
                {{ res.desform }}
              </span>
            </div>
            <p></p>
          </div>
        </div>
          <p></p>
          <span> Разобъём на конъюнкты: </span>
          <p></p>
          <div v-for="(str, k) in resString[i]" v-bind:key="k">
            <div>
              <div> {{ k + 1 }}) Разобъём на дизъюнкты: </div>
              <p></p>
              <div v-for="(dis, n) in str" v-bind:key="n">
                <div style="margin: 0 50px;">
                {{String.fromCharCode(97 + n)}}) {{dis}}
                </div>
              </div>
              <p></p>
            </div>
          </div>
        </div>
        <p></p>
      </div>
    </div>

    <div v-show="isResult">
      <p></p>
      <h5>Выберем нужные нам выражения и преобразуем их</h5>
      <p></p>
      <div style="border: solid">
        <div v-for="str in select" v-bind:key="str">
          <div>
            {{ str }}
          </div>
        </div>
      </div>
      <p></p>
      <h5>Распишем, как все было</h5>
      <p></p>
      <div style="border: solid">
        <p v-for="step in result" v-bind:key="step">
          <span v-for="str in step" v-bind:key="str">
            <div>
              {{ str }}
            </div>
          </span>
        </p>
      </div>
    </div>

      <div v-show="!isResult">
        <p>
          Не удалось найти преступника за разумное время.
        </p>

      </div>
    <p></p>
  </div>

</template>
<script>
  import {url} from "@/main";
  export default {
    props: ['mformulas'],
    name: "ResMethodView",
    data() {
      return {
        alertText: '',
        select: [],
        result: [],
        isResult: false,
        formulas: [],
        resFormulas: [],
        isShow: [],
        resString: []
      }
    },
    created() {
      this.mformulas.forEach(f => {
        this.formulas.push(f);
        this.isShow.push(Object.assign({}, {val: false}));
      })
      this.createData();

    },
    methods: {
      getSelected() {
        this.$http.get(url + "/resmethod/selected").then(response => {
          const stringlist = response && response.data ? response.data : []
          stringlist.forEach(f => {
            this.select.push(f)
          })
        })
      },
      getSteps() {
        this.$http.get(url + "/resmethod/steps").then(response => {
          const stringlist = response && response.data ? response.data : []
          stringlist.forEach(f => {
            this.result.push(f)
          })
        }).then(this.resMethod)
      },
      getData() {
        this.$http.get(url + "/resmethod/transform").then(response => {
          const formulaslist = response && response.data ? response.data : []
          formulaslist.forEach(f => {
            this.resFormulas.push(f.map(r => {
              return {
                id: r.id,
                description: r.description,
                operators: r.operators,
                literals: r.literals,
                logform: r.logform,
                desform: r.desform
              }
            }))
          })
        }).then(this.getDataString())
      },
      getDataString() {
        this.$http.get(url + "/resmethod/str").then(response => {
          const stringlist = response && response.data ? response.data : []
          stringlist.forEach(f => {
            this.resString.push(f)
          })
        }).then(this.getSteps)
      },
      createData() {

        this.$http.post(url + "/resmethod", this.formulas).catch(() => {
          this.alertText = "Ошибка!"
        }).then(
            this.getData
        )
      },
      resMethod() {
        if (this.result.length != 0) {
          this.getSelected();
          this.isResult = true;
        }
      },
      getIndex(list, id) {
        for (var i = 0; i < list.length; i++ ) {
          if (list[i].id == id) {
            return i;
          }
        }
      },
      show(i) {
        if (this.isShow[i].val) {
          return true;
        }
        return false;
      },
      changeShow(i) {
        this.isShow[i].val = !this.isShow[i].val;
      }
    }
  }
</script>
<style>

</style>