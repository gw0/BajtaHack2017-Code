<?php
/* @var $this MeritevController */
/* @var $model Meritev */
/* @var $form CActiveForm */
?>

<div class="form">

<?php $form=$this->beginWidget('CActiveForm', array(
	'id'=>'meritev-form',
	// Please note: When you enable ajax validation, make sure the corresponding
	// controller action is handling ajax validation correctly.
	// There is a call to performAjaxValidation() commented in generated controller code.
	// See class documentation of CActiveForm for details on this.
	'enableAjaxValidation'=>false,
)); ?>

	

	<?php echo $form->errorSummary($model); ?>

	<div class="row">
		<?php echo $form->labelEx($model,'ID'); ?>
		<?php echo $form->textField($model,'ID'); ?>
		<?php echo $form->error($model,'ID'); ?>
	</div>

	<div class="row">
		<?php echo $form->labelEx($model,'SENZOR_ID'); ?>
		<?php echo $form->dropDownList($model, 'SENZOR_ID', CHtml::listData(Senzor::model()->findAll(), 'ID', 'NAZIV')); ?>
		<?php echo $form->error($model,'SENZOR_ID'); ?>
	</div>

	<div class="row">
		<?php echo $form->labelEx($model,'VALUE'); ?>
		<?php echo $form->textField($model,'VALUE',array('size'=>60,'maxlength'=>100)); ?>
		<?php echo $form->error($model,'VALUE'); ?>
	</div>

	<div class="row">
		<?php echo $form->labelEx($model,'CREATED'); ?>
		<?php echo $form->textField($model,'CREATED'); ?>
		<?php echo $form->error($model,'CREATED'); ?>
	</div>

	<div class="row buttons">
		<?php echo CHtml::submitButton($model->isNewRecord ? 'Kreiraj' : 'Save'); ?>
	</div>

<?php $this->endWidget(); ?>

</div><!-- form -->