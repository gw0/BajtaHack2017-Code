<?php

/**
 * This is the model class for table "senzor".
 *
 * The followings are the available columns in table 'senzor':
 * @property integer $ID
 * @property string $NAZIV
 * @property integer $NAPRAVA_ID
 * @property integer $TIP_ID
 * @property string $NASLOV
 *
 * The followings are the available model relations:
 * @property Meritev[] $meritevs
 * @property Pomen[] $pomens
 * @property Naprava $nAPRAVA
 * @property Tip $tIP
 */
class Senzor extends CActiveRecord
{
	/**
	 * @return string the associated database table name
	 */
	public function tableName()
	{
		return 'senzor';
	}

	/**
	 * @return array validation rules for model attributes.
	 */
	public function rules()
	{
		// NOTE: you should only define rules for those attributes that
		// will receive user inputs.
		return array(
			array('NAZIV, NAPRAVA_ID, TIP_ID, NASLOV', 'required'),
			array('NAPRAVA_ID, TIP_ID', 'numerical', 'integerOnly'=>true),
			array('NAZIV, NASLOV', 'length', 'max'=>300),
			// The following rule is used by search().
			// @todo Please remove those attributes that should not be searched.
			array('ID, NAZIV, NAPRAVA_ID, TIP_ID, NASLOV', 'safe', 'on'=>'search'),
		);
	}

	/**
	 * @return array relational rules.
	 */
	public function relations()
	{
		// NOTE: you may need to adjust the relation name and the related
		// class name for the relations automatically generated below.
		return array(
			'meritev' => array(self::HAS_MANY, 'Meritev', 'SENZOR_ID'),
			'pomen' => array(self::HAS_MANY, 'Pomen', 'SENZOR_ID'),
			'naprava' => array(self::BELONGS_TO, 'Naprava', 'NAPRAVA_ID'),
			'tip' => array(self::BELONGS_TO, 'Tip', 'TIP_ID'),
		);
	}

	/**
	 * @return array customized attribute labels (name=>label)
	 */
	public function attributeLabels()
	{
		return array(
			'ID' => 'ID',
			'NAZIV' => 'Naziv',
			'NAPRAVA_ID' => 'Naprava',
			'TIP_ID' => 'Tip',
			'NASLOV' => 'Naslov',
		);
	}

	/**
	 * Retrieves a list of models based on the current search/filter conditions.
	 *
	 * Typical usecase:
	 * - Initialize the model fields with values from filter form.
	 * - Execute this method to get CActiveDataProvider instance which will filter
	 * models according to data in model fields.
	 * - Pass data provider to CGridView, CListView or any similar widget.
	 *
	 * @return CActiveDataProvider the data provider that can return the models
	 * based on the search/filter conditions.
	 */
	public function search()
	{
		// @todo Please modify the following code to remove attributes that should not be searched.

		$criteria=new CDbCriteria;

		$criteria->compare('ID',$this->ID);
		$criteria->compare('NAZIV',$this->NAZIV,true);
		$criteria->compare('NAPRAVA_ID',$this->NAPRAVA_ID);
		$criteria->compare('TIP_ID',$this->TIP_ID);
		$criteria->compare('NASLOV',$this->NASLOV,true);

		return new CActiveDataProvider($this, array(
			'criteria'=>$criteria,
			'pagination'=>array(
                    'pageSize'=>500,
            ),
            'sort'=>array(
				'defaultOrder'=>'NAPRAVA_ID ASC',
			)
		));
	}

	/**
	 * Returns the static model of the specified AR class.
	 * Please note that you should have this exact method in all your CActiveRecord descendants!
	 * @param string $className active record class name.
	 * @return Senzor the static model class
	 */
	public static function model($className=__CLASS__)
	{
		return parent::model($className);
	}
}
